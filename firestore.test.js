const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validUserProfile(uid, ts) {
  return {
    userId: uid,
    displayName: "Elena Vance",
    bio: "Late-night cinema & sci-fi enthusiast.",
    accentColorKey: "AFTERGLOW_PURPLE",
    compactMode: false,
    yearlyMovieGoal: 50,
    yearlySeriesGoal: 12,
    yearlyEpisodeGoal: 300,
    createdAt: ts,
    updatedAt: ts,
  };
}

function validMediaItem(uid, ts, overrides = {}) {
  return {
    userId: uid,
    title: "Dark",
    mediaCategory: "WEB_SERIES",
    posterUrl: "https://image.tmdb.org/t/p/w500/example.jpg",
    genre: "Sci-Fi, Mystery, Thriller",
    language: "German",
    releaseYear: 2017,
    streamingPlatform: "Netflix",
    imdbRating: 8.7,
    personalRating: 9.5,
    runtimeMinutes: 53,
    director: "Baran bo Odar",
    mainCast: "Louis Hofmann, Lisa Vicari",
    country: "Germany",
    watchStatus: "WATCHING",
    startDate: "2026-10-01",
    completionDate: "",
    rewatchCount: 0,
    rewatchFlag: false,
    trailerLink: "https://youtube.com/watch?v=example",
    imdbLink: "https://imdb.com/title/tt5753856",
    personalReview: "Masterpiece of nonlinear storytelling.",
    notes: "Watch Season 2 finale carefully.",
    totalSeasons: 3,
    currentSeason: 1,
    totalEpisodes: 26,
    episodesWatched: 8,
    currentEpisode: 8,
    watchlistItem: false,
    priority: "HIGH",
    dateAdded: "2026-10-01",
    favourite: true,
    allTimeFavourite: false,
    manualRank: 2,
    hiddenGem: false,
    moodTags: ["Mind-Bending", "Dark & Gritty"],
    createdAt: ts,
    updatedAt: ts,
    ...overrides,
  };
}

function validQuote(uid, ts, overrides = {}) {
  return {
    userId: uid,
    quoteText: "What we know is a drop, what we don't know is an ocean.",
    sourceTitle: "Dark",
    characterName: "Adam",
    mediaItemId: "item_1",
    mediaCategory: "WEB_SERIES",
    createdAt: ts,
    updatedAt: ts,
    ...overrides,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const ts = new Date(Date.now() - 2000);
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set(validUserProfile(ALICE_UID, ts)));
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("media_items").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("quotes").get());
});

test("UserProfile: owner create, partial update, cross-user denial, and bounds enforcement", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 10000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validUserProfile(ALICE_UID, created))
  );

  // Partial update allowed fields
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      displayName: "Elena V.",
      accentColorKey: "MIDNIGHT_ROSE",
      yearlyMovieGoal: 60,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt mutation
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost field
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject cross-user read/write
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).update({ displayName: "Hijacked", updatedAt: new Date(Date.now() - 500) }));
});

test("MediaItem: owner CRUD, partial episode increment update, list query, and security rejections", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 10000);

  const itemRef = aliceDb.collection("users").doc(ALICE_UID).collection("media_items").doc("item_dark");
  await assertSucceeds(itemRef.set(validMediaItem(ALICE_UID, created)));

  // List subcollection succeeds for owner (including when empty or populated)
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("media_items").get());

  // Partial update (+1 episode) succeeds
  await assertSucceeds(
    itemRef.update({
      episodesWatched: 9,
      currentEpisode: 9,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject @DocumentId `id` field in payload
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("media_items").doc("item_bad_id").set(
      validMediaItem(ALICE_UID, created, { id: "item_bad_id" })
    )
  );

  // Reject invalid enum or rating out of bounds
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("media_items").doc("item_bad_rating").set(
      validMediaItem(ALICE_UID, created, { personalRating: 11.0 })
    )
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("media_items").doc("item_bad_cat").set(
      validMediaItem(ALICE_UID, created, { mediaCategory: "PODCAST" })
    )
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("media_items").doc("item_future").set(
      validMediaItem(ALICE_UID, new Date(Date.now() + 60000))
    )
  );

  // Reject cross-user list, get, update, delete
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("media_items").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("media_items").doc("item_dark").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("media_items").doc("item_dark").delete());

  // Owner delete succeeds
  await assertSucceeds(itemRef.delete());
});

test("QuoteItem: owner CRUD and cross-user isolation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 10000);

  const quoteRef = aliceDb.collection("users").doc(ALICE_UID).collection("quotes").doc("quote_1");
  await assertSucceeds(quoteRef.set(validQuote(ALICE_UID, created)));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("quotes").get());

  await assertSucceeds(
    quoteRef.update({
      characterName: "Jonas",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("quotes").get());
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("quotes").doc("quote_empty").set(
      validQuote(ALICE_UID, created, { quoteText: "" })
    )
  );

  await assertSucceeds(quoteRef.delete());
});
