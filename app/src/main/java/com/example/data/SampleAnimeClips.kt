package com.example.data

import com.example.model.DubProject
import com.example.model.ProjectCategory
import com.example.model.SubtitleDialogue
import com.example.model.VoiceTone

object SampleAnimeClips {

    val sampleProjects = listOf(
        DubProject(
            id = "proj_flame_awakening",
            title = "The Flame Awakening",
            japaneseTitle = "炎の覚醒 (Honō no Kakusei)",
            hindiTitle = "ज्वाला का जागरण",
            category = ProjectCategory.SHONEN_ACTION,
            durationMs = 18000L,
            bgVideoStyle = "SHONEN_DUEL",
            selectedVoiceProfileId = "voice_shonen_hero",
            originalAudioVolume = 0.20f,
            dubbedAudioVolume = 1.0f,
            dialogues = listOf(
                SubtitleDialogue(
                    id = "d1_1",
                    startTimeMs = 800L,
                    endTimeMs = 4200L,
                    speaker = "Kenji (Hero)",
                    speakerRole = "Hero Protagonist",
                    japaneseText = "諦めるな！俺たちの戦いはまだ終わっていない！",
                    romajiText = "Akirameru na! Oretachi no tatakai wa mada owatte inai!",
                    hindiText = "हार मत मानो! हमारी लड़ाई अभी खत्म नहीं हुई है!",
                    hinglishText = "Haar mat maano! Humari ladai abhi khatam nahi hui hai!",
                    voiceTone = VoiceTone.HEROIC,
                    syncOffsetMs = 0,
                    speechRate = 1.05f
                ),
                SubtitleDialogue(
                    id = "d1_2",
                    startTimeMs = 4800L,
                    endTimeMs = 8600L,
                    speaker = "Akuma (Demon)",
                    speakerRole = "Demon Antagonist",
                    japaneseText = "無駄だ。貴様のような人間に、我が力は止められん！",
                    romajiText = "Muda da. Kisama no yō na ningen ni, waga chikara wa tomeraren!",
                    hindiText = "बेकार है! तुम जैसे मामूली इंसान मेरी ताकत को नहीं रोक सकते!",
                    hinglishText = "Bekaar hai! Tum jaise maamooli insaan meri taakat ko nahi rok sakte!",
                    voiceTone = VoiceTone.DEEP_VILLAIN,
                    syncOffsetMs = 50,
                    speechRate = 0.95f
                ),
                SubtitleDialogue(
                    id = "d1_3",
                    startTimeMs = 9200L,
                    endTimeMs = 13500L,
                    speaker = "Kenji (Hero)",
                    speakerRole = "Hero Protagonist",
                    japaneseText = "燃え上がれ、我が魂！炎の呼吸・壱ノ型！",
                    romajiText = "Moeagare, waga tamashī! Honō no Kokyū: Ichi no Kata!",
                    hindiText = "भड़क उठो, मेरी आत्मा! अग्नि श्वास का प्रथम रूप!",
                    hinglishText = "Bhadak utho, meri aatma! Agni Shwaas ka Pratham Roop!",
                    voiceTone = VoiceTone.INTENSE_SHOUT,
                    syncOffsetMs = 0,
                    speechRate = 1.12f
                ),
                SubtitleDialogue(
                    id = "d1_4",
                    startTimeMs = 14200L,
                    endTimeMs = 17500L,
                    speaker = "Master Kenshin",
                    speakerRole = "Sensei",
                    japaneseText = "見事だ、ケンジ。お前の心が炎を宿した。",
                    romajiText = "Migoto da, Kenji. Omae no kokoro ga honō o yadoshita.",
                    hindiText = "शानदार, केंजी! तुम्हारे दिल ने सचमुच आग को जगा लिया है।",
                    hinglishText = "Shaandaar, Kenji! Tumhare dil ne sachmuch aag ko jaga liya hai.",
                    voiceTone = VoiceTone.CALM_SENSEI,
                    syncOffsetMs = -40,
                    speechRate = 0.90f
                )
            )
        ),

        DubProject(
            id = "proj_rainy_neotokyo",
            title = "Rainy Neo-Tokyo Cafe",
            japaneseTitle = "雨のネオ東京 (Ame no Neo Tōkyō)",
            hindiTitle = "बारिश में भीगा नियो-टोक्यो",
            category = ProjectCategory.CYBERPUNK_SCI_FI,
            durationMs = 16000L,
            bgVideoStyle = "CYBERPUNK_CAFE",
            selectedVoiceProfileId = "voice_tsundere_heroine",
            originalAudioVolume = 0.30f,
            dubbedAudioVolume = 1.0f,
            dialogues = listOf(
                SubtitleDialogue(
                    id = "d2_1",
                    startTimeMs = 1000L,
                    endTimeMs = 4500L,
                    speaker = "Aoi (Cyber Agent)",
                    speakerRole = "Tsundere Agent",
                    japaneseText = "遅いわよ！どれだけ待たせる気？",
                    romajiText = "Osoi wa yo! Dore dake mataseru ki?",
                    hindiText = "बहुत देर कर दी तुमने! कितनी देर इंतज़ार कराओगे?!",
                    hinglishText = "Bahut der kar di tumne! Kitni der intezaar karaoge?!",
                    voiceTone = VoiceTone.KAWAII_TSUNDERE,
                    syncOffsetMs = 20,
                    speechRate = 1.08f
                ),
                SubtitleDialogue(
                    id = "d2_2",
                    startTimeMs = 5200L,
                    endTimeMs = 9400L,
                    speaker = "Detective Ren",
                    speakerRole = "Detective",
                    japaneseText = "すまない。シンジケートの監視ドローンを巻いてきたんだ。",
                    romajiText = "Sumanai. Shinjikēto no kanshi dorōn o maite kitan da.",
                    hindiText = "माफ करना। सिंडिकेट के निगरानी ड्रोन से बचकर आ रहा था।",
                    hinglishText = "Maaf karna. Syndicate ke nigrani drone se bachkar aa raha tha.",
                    voiceTone = VoiceTone.HEROIC,
                    syncOffsetMs = -20,
                    speechRate = 0.98f
                ),
                SubtitleDialogue(
                    id = "d2_3",
                    startTimeMs = 10200L,
                    endTimeMs = 15200L,
                    speaker = "Aoi (Cyber Agent)",
                    speakerRole = "Tsundere Agent",
                    japaneseText = "フン…怪我はないの？べ、別に心配したわけじゃないからね！",
                    romajiText = "Hun... Kega wa nai no? Be, betsu ni shinpai shita wake ja nai kara ne!",
                    hindiText = "हूँफ... चोट तो नहीं लगी? मै-मैं कोई तुम्हारी चिंता नहीं कर रही थी!",
                    hinglishText = "Hmph... Chot toh nahi lagi? Mai koi tumhari chinta nahi kar rahi thi!",
                    voiceTone = VoiceTone.KAWAII_TSUNDERE,
                    syncOffsetMs = 10,
                    speechRate = 1.15f
                )
            )
        ),

        DubProject(
            id = "proj_samurai_forest",
            title = "Forest of the Silent Blade",
            japaneseTitle = "静寂の刀 (Seijaku no Katana)",
            hindiTitle = "शांत तलवार का वन",
            category = ProjectCategory.SAMURAI_HISTORICAL,
            durationMs = 17000L,
            bgVideoStyle = "SAMURAI_BAMBOO",
            selectedVoiceProfileId = "voice_calm_sensei",
            originalAudioVolume = 0.25f,
            dubbedAudioVolume = 1.0f,
            dialogues = listOf(
                SubtitleDialogue(
                    id = "d3_1",
                    startTimeMs = 1200L,
                    endTimeMs = 5200L,
                    speaker = "Wandering Swordsman",
                    speakerRole = "Ronin",
                    japaneseText = "刀を抜け。ここでお前との因縁を断ち切る。",
                    romajiText = "Katana o nuke. Koko de omae to no innen o tachikiru.",
                    hindiText = "तलवार निकालो। आज तुम्हारे साथ यह पुराना हिसाब यहीं खत्म होगा।",
                    hinglishText = "Talwaar nikaalo. Aaj tumhare saath yeh purana hisaab yahi khatam hoga.",
                    voiceTone = VoiceTone.DEEP_VILLAIN,
                    syncOffsetMs = 0,
                    speechRate = 0.94f
                ),
                SubtitleDialogue(
                    id = "d3_2",
                    startTimeMs = 6000L,
                    endTimeMs = 10500L,
                    speaker = "Master Kenshin",
                    speakerRole = "Sensei",
                    japaneseText = "憎しみからは何も生まれん。刃を納めよ。",
                    romajiText = "Nikushimi kara wa nani mo umaren. Yaiba o osameyo.",
                    hindiText = "नफ़रत से कुछ हासिल नहीं होता। अपनी तलवार को म्यान में वापस रखो।",
                    hinglishText = "Nafrat se kuch haasil nahi hota. Apni talwaar ko myaan mein wapas rakho.",
                    voiceTone = VoiceTone.CALM_SENSEI,
                    syncOffsetMs = -30,
                    speechRate = 0.88f
                ),
                SubtitleDialogue(
                    id = "d3_3",
                    startTimeMs = 11200L,
                    endTimeMs = 16000L,
                    speaker = "Narrator",
                    speakerRole = "Narrator",
                    japaneseText = "風が竹林を揺らし、二人の武士は互いの気迫を測り合っていた。",
                    romajiText = "Kaze ga chikurin o yurashi, futari no bushi wa otagai no kihaku o hakariatte ita.",
                    hindiText = "हवा ने बांस के जंगल को हिला दिया, और दो योद्धा एक-दूसरे के संकल्प को परखने लगे।",
                    hinglishText = "Hawa ne baans ke jungle ko hila diya, aur do yoddha sankalp ko parakhne lage.",
                    voiceTone = VoiceTone.ENERGETIC_NARRATOR,
                    syncOffsetMs = 0,
                    speechRate = 1.0f
                )
            )
        )
    )
}
