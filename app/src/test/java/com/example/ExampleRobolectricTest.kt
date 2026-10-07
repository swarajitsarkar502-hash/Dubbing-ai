package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DefaultVoiceProfiles
import com.example.data.SampleAnimeClips
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DubVani", appName)
  }

  @Test
  fun `sample anime clips load with dialogues`() {
    val projects = SampleAnimeClips.sampleProjects
    assertTrue(projects.isNotEmpty())
    val firstProject = projects.first()
    assertTrue(firstProject.dialogues.isNotEmpty())
    val firstDialogue = firstProject.dialogues.first()
    assertNotNull(firstDialogue.japaneseText)
    assertNotNull(firstDialogue.hindiText)
  }

  @Test
  fun `voice clone profiles load properly`() {
    val profiles = DefaultVoiceProfiles.profiles
    assertTrue(profiles.isNotEmpty())
    val hero = DefaultVoiceProfiles.getProfile("voice_shonen_hero")
    assertEquals("voice_shonen_hero", hero.id)
  }
}
