package com.example

import com.example.ui.screens.APP_VERSION
import com.example.ui.screens.ZenixUpdateConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateConfigTest {

    @Test
    fun compareSemver_handlesNumericSegmentsProperly() {
        assertEquals(0, ZenixUpdateConfig.compareSemver("2.5.0", "2.5.0"))
        assertEquals(0, ZenixUpdateConfig.compareSemver("v2.5.0", "2.5"))
        assertTrue(ZenixUpdateConfig.compareSemver("2.10.0", "2.5.0") > 0)
        assertTrue(ZenixUpdateConfig.compareSemver("2.4.9", "2.5.0") < 0)
        assertTrue(ZenixUpdateConfig.compareSemver("3.0.0", "2.99.99") > 0)
        assertTrue(ZenixUpdateConfig.compareSemver("2.5.1-beta", "2.5.0") > 0)
    }

    @Test
    fun updateAvailability_andBadgeLogic_matchInstalledVersion() {
        assertEquals("2.5.0", APP_VERSION)
        assertEquals(APP_VERSION, ZenixUpdateConfig.currentVersion)

        // With APP_VERSION == latestVersion ("2.5.0"), no update is pending and no NEW badge shows.
        assertFalse(ZenixUpdateConfig.isUpdateAvailable(APP_VERSION))
        assertFalse(ZenixUpdateConfig.shouldShowUpdateBadge(lastSeenVersion = "", installedVersion = APP_VERSION))
        assertFalse(ZenixUpdateConfig.shouldShowUpdateBadge(lastSeenVersion = "2.5.0", installedVersion = APP_VERSION))
        assertFalse(ZenixUpdateConfig.shouldShowUpdateBadge(lastSeenVersion = null, installedVersion = APP_VERSION))

        // When an older version is installed (< latestVersion), badge shows only until seen.
        assertTrue(ZenixUpdateConfig.isUpdateAvailable("2.4.0"))
        assertTrue(ZenixUpdateConfig.shouldShowUpdateBadge(lastSeenVersion = "", installedVersion = "2.4.0"))
        assertTrue(ZenixUpdateConfig.shouldShowUpdateBadge(lastSeenVersion = "2.4.0", installedVersion = "2.4.0"))
        assertFalse(
            ZenixUpdateConfig.shouldShowUpdateBadge(
                lastSeenVersion = ZenixUpdateConfig.latestVersion,
                installedVersion = "2.4.0"
            )
        )
    }
}
