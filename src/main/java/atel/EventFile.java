package atel;

import main.DataWritingManager;
import model.Nameable;
import model.strings.FieldString;
import model.strings.LocalizedFieldStringObject;
import reading.Chunk;
import reading.BytesHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static main.StringHelper.MACRO_LOOKUP;

/**
 * jppc/event/.../.ebp
 */
public class EventFile implements Nameable {
    public static final int DEFAULT_ASSUMED_CHUNK_COUNT = 10;
    public static Map<Integer, String> EVENT_BY_ROOM_ID;

    public String id;
    public AtelScriptObject eventScript;
    int[] scriptBytes;
    int[] japaneseTextBytes;
    int[] unknownChunk2Bytes;
    int[] ftcxBytes;
    int[] englishTextBytes;
    public List<LocalizedFieldStringObject> strings;

    public EventFile(String id, int[] bytes) {
        this.id = id;
        List<Chunk> chunks = BytesHelper.bytesToChunks(bytes, DEFAULT_ASSUMED_CHUNK_COUNT, 4);
        mapChunks(chunks);
        mapObjects();
        mapStrings();
    }

    private void mapChunks(List<Chunk> chunks) {
        scriptBytes = chunks.get(0).bytes;
        japaneseTextBytes = chunks.get(1).bytes;
        unknownChunk2Bytes = chunks.get(2).bytes;
        ftcxBytes = chunks.get(3).bytes;
        if (chunks.size() > 4 && chunks.get(4).offset != 0) {
            englishTextBytes = chunks.get(4).bytes;
        }
    }

    private void mapObjects() {
        eventScript = new AtelScriptObject(scriptBytes, null);
    }

    private void mapStrings() {
        List<FieldString> japaneseStrings = FieldString.fromStringData(japaneseTextBytes, false, "jp");
        if (japaneseStrings != null) {
            List<LocalizedFieldStringObject> localizedJpStringObjects = japaneseStrings.stream().map(str -> new LocalizedFieldStringObject("jp", str)).collect(Collectors.toList());
            addLocalizations(localizedJpStringObjects);
        }
        List<FieldString> englishStrings = FieldString.fromStringData(englishTextBytes, false, "us");
        if (englishStrings != null) {
            List<LocalizedFieldStringObject> localizedUsStringObjects = englishStrings.stream().map(str -> new LocalizedFieldStringObject("us", str)).collect(Collectors.toList());
            addLocalizations(localizedUsStringObjects);
        }
    }

    public void addLocalizations(List<LocalizedFieldStringObject> strings) {
        if (this.strings == null) {
            this.strings = strings;
            return;
        }
        for (int i = 0; i < strings.size(); i++) {
            LocalizedFieldStringObject localizationStringObject = strings.get(i);
            if (i < this.strings.size()) {
                LocalizedFieldStringObject stringObject = this.strings.get(i);
                if (stringObject != null && localizationStringObject != null) {
                    localizationStringObject.copyInto(stringObject);
                }
            } else {
                this.strings.add(localizationStringObject);
            }
        }
    }

    public void parseScript() {
        if (eventScript != null) {
            eventScript.setStrings(strings);
            eventScript.parseScript();
        }
    }

    public int[] toBytes() {
        // Note: this is untested and probably doesn't work!
        List<int[]> chunks = new ArrayList<>();
        chunks.add(scriptBytes);
        chunks.add(DataWritingManager.stringsToStringFileBytes(strings, "jp"));
        chunks.add(unknownChunk2Bytes);
        chunks.add(ftcxBytes);
        chunks.add(DataWritingManager.stringsToStringFileBytes(strings, "us"));
        return BytesHelper.chunksToBytes(chunks, -1, 0x40, 0x10);
    }

    @Override
    public String toString() {
        StringBuilder full = new StringBuilder();
        full.append(getName()).append('\n');
        if (eventScript != null) {
            full.append("- Script Code -").append('\n');
            full.append(eventScript.allLinesString());
            full.append("- Script Workers -").append('\n');
            full.append(eventScript.workersString()).append('\n');
        } else {
            full.append("Event Script missing");
        }
        return full.toString();
    }

    public String getName(String localization) {
        if (eventScript == null || eventScript.areaNameIndexes == null || eventScript.areaNameIndexes.isEmpty()) {
            return id;
        }
        String areaName = MACRO_LOOKUP.get(0xB00 + eventScript.areaNameIndexes.get(0)).getLocalizedString(localization);
        return id + " (" + areaName + ")";
    }

    public static String getRoomNameById(int id) {
        prepareMapOfEvents();
        return EVENT_BY_ROOM_ID.get(id);
    }

    private static void prepareMapOfEvents() {
        if (EVENT_BY_ROOM_ID != null) {
            return;
        }
        EVENT_BY_ROOM_ID = new HashMap<>();
        EVENT_BY_ROOM_ID.put(0, "event00");
        EVENT_BY_ROOM_ID.put(1, "startmap0");
        EVENT_BY_ROOM_ID.put(2, "camtest0");

        EVENT_BY_ROOM_ID.put(16, "ptkl0200");
        EVENT_BY_ROOM_ID.put(17, "bsvr0000");
        EVENT_BY_ROOM_ID.put(18, "klyt0000");
        EVENT_BY_ROOM_ID.put(19, "bsil0100");
        EVENT_BY_ROOM_ID.put(20, "bsil0200");
        EVENT_BY_ROOM_ID.put(21, "bsil0400");
        EVENT_BY_ROOM_ID.put(22, "bsil0500");
        EVENT_BY_ROOM_ID.put(23, "test20");

        EVENT_BY_ROOM_ID.put(41, "bsil0300");
        EVENT_BY_ROOM_ID.put(42, "bsyt0000");
        EVENT_BY_ROOM_ID.put(43, "ptkl0100");
        EVENT_BY_ROOM_ID.put(44, "klyt0900");
        EVENT_BY_ROOM_ID.put(45, "klyt1100");
        EVENT_BY_ROOM_ID.put(46, "ptkl1300");
        EVENT_BY_ROOM_ID.put(47, "ptkl1600");
        EVENT_BY_ROOM_ID.put(48, "bjyt0000");
        EVENT_BY_ROOM_ID.put(49, "bjyt0200");
        EVENT_BY_ROOM_ID.put(50, "bjyt0300");
        EVENT_BY_ROOM_ID.put(51, "bjyt0500");
        EVENT_BY_ROOM_ID.put(52, "bjyt0600");
        EVENT_BY_ROOM_ID.put(53, "ptkl1000");
        EVENT_BY_ROOM_ID.put(54, "maca0400");
        EVENT_BY_ROOM_ID.put(55, "lchb0600");
        EVENT_BY_ROOM_ID.put(56, "nagi0500");
        EVENT_BY_ROOM_ID.put(57, "lchb0700");
        EVENT_BY_ROOM_ID.put(58, "mihn0200");
        EVENT_BY_ROOM_ID.put(59, "mihn0600");
        EVENT_BY_ROOM_ID.put(60, "bsvr0200");
        EVENT_BY_ROOM_ID.put(61, "slik0200");
        EVENT_BY_ROOM_ID.put(62, "bltz0000");
        EVENT_BY_ROOM_ID.put(63, "bjyt0400");
        EVENT_BY_ROOM_ID.put(64, "cdsp0700");
        EVENT_BY_ROOM_ID.put(65, "klyt0100");

        EVENT_BY_ROOM_ID.put(67, "bsil0600");
        EVENT_BY_ROOM_ID.put(68, "bsvr0300");
        EVENT_BY_ROOM_ID.put(69, "bsil0700");
        EVENT_BY_ROOM_ID.put(70, "bsil0000");
        EVENT_BY_ROOM_ID.put(71, "cdsp0000");
        EVENT_BY_ROOM_ID.put(72, "lchi1300");
        EVENT_BY_ROOM_ID.put(73, "lchb1400");
        EVENT_BY_ROOM_ID.put(74, "bjyt0700");
        EVENT_BY_ROOM_ID.put(75, "genk0000");
        EVENT_BY_ROOM_ID.put(76, "djyt0000");
        EVENT_BY_ROOM_ID.put(77, "lchb1800");
        EVENT_BY_ROOM_ID.put(78, "klyt0300");
        EVENT_BY_ROOM_ID.put(79, "kino0000");
        EVENT_BY_ROOM_ID.put(80, "mcyt0600");
        EVENT_BY_ROOM_ID.put(81, "djyt0300");
        EVENT_BY_ROOM_ID.put(82, "djyt0100");
        EVENT_BY_ROOM_ID.put(83, "bsvr0001");
        EVENT_BY_ROOM_ID.put(84, "bsyt0001");
        EVENT_BY_ROOM_ID.put(85, "lchb0100");
        EVENT_BY_ROOM_ID.put(86, "lchb0200");
        EVENT_BY_ROOM_ID.put(87, "lchb0300");
        EVENT_BY_ROOM_ID.put(88, "lchb0400");
        EVENT_BY_ROOM_ID.put(89, "lchb0500");
        EVENT_BY_ROOM_ID.put(90, "djyt0700");
        EVENT_BY_ROOM_ID.put(91, "djyt0800");
        EVENT_BY_ROOM_ID.put(92, "kino0100");
        EVENT_BY_ROOM_ID.put(93, "kino0400");
        EVENT_BY_ROOM_ID.put(94, "swin0200");
        EVENT_BY_ROOM_ID.put(95, "mihn0000");
        EVENT_BY_ROOM_ID.put(96, "klyt0500");
        EVENT_BY_ROOM_ID.put(97, "genk1600");
        EVENT_BY_ROOM_ID.put(98, "ptkl0600");
        EVENT_BY_ROOM_ID.put(99, "genk0900");
        EVENT_BY_ROOM_ID.put(100, "bsvr0100");
        EVENT_BY_ROOM_ID.put(102, "maca0200");
        EVENT_BY_ROOM_ID.put(103, "bsyt0500");
        EVENT_BY_ROOM_ID.put(104, "luca0100");
        EVENT_BY_ROOM_ID.put(105, "genk0100");
        EVENT_BY_ROOM_ID.put(106, "mcyt0200");
        EVENT_BY_ROOM_ID.put(107, "luca0600");
        EVENT_BY_ROOM_ID.put(108, "klyt1000");
        EVENT_BY_ROOM_ID.put(109, "genk1500");
        EVENT_BY_ROOM_ID.put(110, "mcfr0000");

        EVENT_BY_ROOM_ID.put(112, "mihn0100");
        EVENT_BY_ROOM_ID.put(113, "cdsp0200");

        EVENT_BY_ROOM_ID.put(115, "mihn0400");
        EVENT_BY_ROOM_ID.put(116, "mihn0500");
        EVENT_BY_ROOM_ID.put(117, "bsyt0600");
        EVENT_BY_ROOM_ID.put(118, "klyt1200");
        EVENT_BY_ROOM_ID.put(119, "kino0200");
        EVENT_BY_ROOM_ID.put(120, "mihn0700");
        EVENT_BY_ROOM_ID.put(121, "lchb0800");
        EVENT_BY_ROOM_ID.put(122, "bsyt0100");
        EVENT_BY_ROOM_ID.put(123, "lchb0000");
        EVENT_BY_ROOM_ID.put(124, "lchb0801");
        EVENT_BY_ROOM_ID.put(125, "lchb0802");
        EVENT_BY_ROOM_ID.put(126, "lchb0803");
        EVENT_BY_ROOM_ID.put(127, "mihn0800");
        EVENT_BY_ROOM_ID.put(128, "kino0500");
        EVENT_BY_ROOM_ID.put(129, "bika0000");
        EVENT_BY_ROOM_ID.put(130, "bika0400");
        EVENT_BY_ROOM_ID.put(131, "kino0700");
        EVENT_BY_ROOM_ID.put(132, "znkd0600");
        EVENT_BY_ROOM_ID.put(133, "bsvr0002");
        EVENT_BY_ROOM_ID.put(134, "znkd0700");
        EVENT_BY_ROOM_ID.put(135, "guad0000");
        EVENT_BY_ROOM_ID.put(136, "bika0100");
        EVENT_BY_ROOM_ID.put(137, "bika0200");
        EVENT_BY_ROOM_ID.put(138, "bika0300");
        EVENT_BY_ROOM_ID.put(139, "ptkl0000");
        EVENT_BY_ROOM_ID.put(140, "kami0000");
        EVENT_BY_ROOM_ID.put(141, "guad0600");
        EVENT_BY_ROOM_ID.put(142, "bsvr0400");
        EVENT_BY_ROOM_ID.put(143, "bsvr0500");
        EVENT_BY_ROOM_ID.put(144, "bsvr0600");
        EVENT_BY_ROOM_ID.put(145, "bsvr0900");
        EVENT_BY_ROOM_ID.put(146, "bsyt0300");
        EVENT_BY_ROOM_ID.put(147, "bsyt0400");
        EVENT_BY_ROOM_ID.put(148, "slik0400");
        EVENT_BY_ROOM_ID.put(149, "slik0700");
        EVENT_BY_ROOM_ID.put(150, "slik0800");
        EVENT_BY_ROOM_ID.put(151, "ptkl0800");
        EVENT_BY_ROOM_ID.put(152, "ptkl1400");
        EVENT_BY_ROOM_ID.put(153, "mcyt0000");
        EVENT_BY_ROOM_ID.put(154, "slik0300");
        EVENT_BY_ROOM_ID.put(155, "klyt0600");
        EVENT_BY_ROOM_ID.put(156, "klyt0700");
        EVENT_BY_ROOM_ID.put(157, "lchb0900");
        EVENT_BY_ROOM_ID.put(158, "lchb1100");
        EVENT_BY_ROOM_ID.put(159, "luca0400");
        EVENT_BY_ROOM_ID.put(160, "djyt0400");
        EVENT_BY_ROOM_ID.put(161, "djyt0500");
        EVENT_BY_ROOM_ID.put(162, "kami0300");
        EVENT_BY_ROOM_ID.put(163, "guad0500");
        EVENT_BY_ROOM_ID.put(164, "maca0000");
        EVENT_BY_ROOM_ID.put(165, "znkd0500");
        EVENT_BY_ROOM_ID.put(166, "klyt0200");
        EVENT_BY_ROOM_ID.put(167, "swin0700");
        EVENT_BY_ROOM_ID.put(168, "swin0800");
        EVENT_BY_ROOM_ID.put(169, "swin0900");
        EVENT_BY_ROOM_ID.put(170, "lchb1000");
        EVENT_BY_ROOM_ID.put(171, "mihn0300");
        EVENT_BY_ROOM_ID.put(172, "guad0200");
        EVENT_BY_ROOM_ID.put(173, "guad0300");
        EVENT_BY_ROOM_ID.put(174, "guad0400");
        EVENT_BY_ROOM_ID.put(175, "ikai0000");
        EVENT_BY_ROOM_ID.put(176, "mcfr0500");
        EVENT_BY_ROOM_ID.put(177, "mcfr0700");
        EVENT_BY_ROOM_ID.put(178, "mcyt0300");
        EVENT_BY_ROOM_ID.put(179, "mcyt0400");
        EVENT_BY_ROOM_ID.put(180, "bvyt0200");
        EVENT_BY_ROOM_ID.put(181, "bvyt0300");
        EVENT_BY_ROOM_ID.put(182, "bvyt0400");
        EVENT_BY_ROOM_ID.put(183, "mcfr1100");
        EVENT_BY_ROOM_ID.put(184, "lchb1200");
        EVENT_BY_ROOM_ID.put(185, "swin0300");

        EVENT_BY_ROOM_ID.put(187, "genk0400");
        EVENT_BY_ROOM_ID.put(188, "genk0500");
        EVENT_BY_ROOM_ID.put(189, "genk1100");
        EVENT_BY_ROOM_ID.put(190, "genk1200");
        EVENT_BY_ROOM_ID.put(191, "znkd0400");
        EVENT_BY_ROOM_ID.put(192, "maca0300");
        EVENT_BY_ROOM_ID.put(193, "ikai0600");
        EVENT_BY_ROOM_ID.put(194, "hiku0800");
        EVENT_BY_ROOM_ID.put(195, "bvyt1000");
        EVENT_BY_ROOM_ID.put(196, "bjyt1000");
        EVENT_BY_ROOM_ID.put(197, "guad0700");
        EVENT_BY_ROOM_ID.put(198, "bvyt0900");
        EVENT_BY_ROOM_ID.put(199, "ssbt0000");
        EVENT_BY_ROOM_ID.put(200, "ssbt0100");
        EVENT_BY_ROOM_ID.put(201, "ssbt0200");
        EVENT_BY_ROOM_ID.put(202, "ssbt0300");
        EVENT_BY_ROOM_ID.put(203, "sins0200");
        EVENT_BY_ROOM_ID.put(204, "sins0400");
        EVENT_BY_ROOM_ID.put(205, "bvyt0000");
        EVENT_BY_ROOM_ID.put(206, "mcfr1200");
        EVENT_BY_ROOM_ID.put(207, "bvyt0500");
        EVENT_BY_ROOM_ID.put(208, "stbv0100");
        EVENT_BY_ROOM_ID.put(209, "stbv0000");
        EVENT_BY_ROOM_ID.put(210, "djyt0200");
        EVENT_BY_ROOM_ID.put(211, "hiku0000");
        EVENT_BY_ROOM_ID.put(212, "bltz0201");
        EVENT_BY_ROOM_ID.put(213, "ikai0800");
        EVENT_BY_ROOM_ID.put(214, "djyt0600");
        EVENT_BY_ROOM_ID.put(215, "maca0100");

        EVENT_BY_ROOM_ID.put(217, "guad0601");
        EVENT_BY_ROOM_ID.put(218, "kino0600");
        EVENT_BY_ROOM_ID.put(219, "azit0400");
        EVENT_BY_ROOM_ID.put(220, "slik1000");
        EVENT_BY_ROOM_ID.put(221, "mcfr0400");
        EVENT_BY_ROOM_ID.put(222, "dome0000");
        EVENT_BY_ROOM_ID.put(223, "nagi0000");
        EVENT_BY_ROOM_ID.put(224, "dome0400");
        EVENT_BY_ROOM_ID.put(225, "zkrn0200");
        EVENT_BY_ROOM_ID.put(226, "bvyt1200");
        EVENT_BY_ROOM_ID.put(227, "bvyt1300");

        EVENT_BY_ROOM_ID.put(230, "lchb0806");

        EVENT_BY_ROOM_ID.put(234, "genk0200");
        EVENT_BY_ROOM_ID.put(235, "genk0600");
        EVENT_BY_ROOM_ID.put(236, "genk1300");
        EVENT_BY_ROOM_ID.put(237, "swin0400");
        EVENT_BY_ROOM_ID.put(238, "mcfr0900");
        EVENT_BY_ROOM_ID.put(239, "mcyt0500");
        EVENT_BY_ROOM_ID.put(240, "mmmc0000");
        EVENT_BY_ROOM_ID.put(241, "mcfr0100");
        EVENT_BY_ROOM_ID.put(242, "mcfr0200");
        EVENT_BY_ROOM_ID.put(243, "guad0100");
        EVENT_BY_ROOM_ID.put(244, "mtgz0100");
        EVENT_BY_ROOM_ID.put(245, "djyt0900");

        EVENT_BY_ROOM_ID.put(247, "kino0300");
        EVENT_BY_ROOM_ID.put(248, "mcfr0300");
        EVENT_BY_ROOM_ID.put(249, "znkd0300");
        EVENT_BY_ROOM_ID.put(250, "lchb0601");

        EVENT_BY_ROOM_ID.put(252, "bsmm0000");
        EVENT_BY_ROOM_ID.put(253, "msmm0000");
        EVENT_BY_ROOM_ID.put(254, "kino0900");
        EVENT_BY_ROOM_ID.put(255, "hiku0001");
        EVENT_BY_ROOM_ID.put(256, "kami0400");
        EVENT_BY_ROOM_ID.put(257, "ikai0100");
        EVENT_BY_ROOM_ID.put(258, "omeg0000");
        EVENT_BY_ROOM_ID.put(259, "mtgz0000");
        EVENT_BY_ROOM_ID.put(260, "maca0401");
        EVENT_BY_ROOM_ID.put(261, "hiku1600");
        EVENT_BY_ROOM_ID.put(262, "kami0000");
        EVENT_BY_ROOM_ID.put(263, "kami0100");
        EVENT_BY_ROOM_ID.put(264, "kami0200");
        EVENT_BY_ROOM_ID.put(265, "hiku0500");
        EVENT_BY_ROOM_ID.put(266, "nagi0400");
        EVENT_BY_ROOM_ID.put(267, "lchb0201");
        EVENT_BY_ROOM_ID.put(268, "lchb0301");
        EVENT_BY_ROOM_ID.put(269, "mcfr1300");
        EVENT_BY_ROOM_ID.put(270, "dome0600");
        EVENT_BY_ROOM_ID.put(271, "omeg0100");
        EVENT_BY_ROOM_ID.put(272, "mtgz0600");
        EVENT_BY_ROOM_ID.put(273, "mcfr0600");
        EVENT_BY_ROOM_ID.put(274, "mcfr0000");
        EVENT_BY_ROOM_ID.put(275, "azit0700");
        EVENT_BY_ROOM_ID.put(276, "azit0000");
        EVENT_BY_ROOM_ID.put(277, "hiku1500");

        EVENT_BY_ROOM_ID.put(279, "nagi0100");
        EVENT_BY_ROOM_ID.put(280, "azit0300");

        EVENT_BY_ROOM_ID.put(282, "slik1100");
        EVENT_BY_ROOM_ID.put(283, "nagi0600");
        EVENT_BY_ROOM_ID.put(284, "mcyt0700");
        EVENT_BY_ROOM_ID.put(285, "mtgz0200");
        EVENT_BY_ROOM_ID.put(286, "azit0600");
        EVENT_BY_ROOM_ID.put(287, "bvyt0600");
        EVENT_BY_ROOM_ID.put(288, "cdsp0100");
        EVENT_BY_ROOM_ID.put(289, "kino0800");
        EVENT_BY_ROOM_ID.put(290, "lmyt0000");
        EVENT_BY_ROOM_ID.put(291, "genk1000");
        EVENT_BY_ROOM_ID.put(292, "gameover");
        EVENT_BY_ROOM_ID.put(293, "ptkl1700");
        EVENT_BY_ROOM_ID.put(294, "ptkl1800");
        EVENT_BY_ROOM_ID.put(295, "bjyt0800");
        EVENT_BY_ROOM_ID.put(296, "sins0300");
        EVENT_BY_ROOM_ID.put(297, "znkd0000");
        EVENT_BY_ROOM_ID.put(298, "bjyt1200");
        EVENT_BY_ROOM_ID.put(299, "lchb1500");

        EVENT_BY_ROOM_ID.put(301, "slik0000");
        EVENT_BY_ROOM_ID.put(302, "swin0000");
        EVENT_BY_ROOM_ID.put(303, "azit0500");

        EVENT_BY_ROOM_ID.put(305, "bvyt0100");
        EVENT_BY_ROOM_ID.put(306, "bvyt1100");
        EVENT_BY_ROOM_ID.put(307, "nagi0700");
        EVENT_BY_ROOM_ID.put(308, "lmyt0100");
        EVENT_BY_ROOM_ID.put(309, "mtgz0300");
        EVENT_BY_ROOM_ID.put(310, "mtgz0700");
        EVENT_BY_ROOM_ID.put(311, "mtgz0800");
        EVENT_BY_ROOM_ID.put(312, "mtgz0900");
        EVENT_BY_ROOM_ID.put(313, "zkrn0100");
        EVENT_BY_ROOM_ID.put(314, "zkrn0300");
        EVENT_BY_ROOM_ID.put(315, "zkrn0600");
        EVENT_BY_ROOM_ID.put(316, "dome0100");
        EVENT_BY_ROOM_ID.put(317, "dome0200");
        EVENT_BY_ROOM_ID.put(318, "dome0300");
        EVENT_BY_ROOM_ID.put(319, "dome0500");
        EVENT_BY_ROOM_ID.put(320, "dome0700");

        EVENT_BY_ROOM_ID.put(322, "sins0000");

        EVENT_BY_ROOM_ID.put(324, "sins0500");
        EVENT_BY_ROOM_ID.put(325, "sins0600");
        EVENT_BY_ROOM_ID.put(326, "sins0700");
        EVENT_BY_ROOM_ID.put(327, "sins0900");

        EVENT_BY_ROOM_ID.put(329, "mcfr1400");
        EVENT_BY_ROOM_ID.put(330, "stmm0100");
        EVENT_BY_ROOM_ID.put(331, "mamm0000");
        EVENT_BY_ROOM_ID.put(332, "kamm0300");
        EVENT_BY_ROOM_ID.put(333, "gemm0100");
        EVENT_BY_ROOM_ID.put(334, "mimm0200");
        EVENT_BY_ROOM_ID.put(335, "lumm0100");
        EVENT_BY_ROOM_ID.put(336, "slmm0200");
        EVENT_BY_ROOM_ID.put(337, "bvmm0000");
        EVENT_BY_ROOM_ID.put(338, "mamm0300");
        EVENT_BY_ROOM_ID.put(339, "bvmm0600");
        EVENT_BY_ROOM_ID.put(340, "bltz0001");
        EVENT_BY_ROOM_ID.put(341, "kimm0000");

        EVENT_BY_ROOM_ID.put(347, "bltz0200");

        EVENT_BY_ROOM_ID.put(349, "loopdemo");

        EVENT_BY_ROOM_ID.put(351, "hiku0200");
        EVENT_BY_ROOM_ID.put(352, "bltz0002");

        EVENT_BY_ROOM_ID.put(355, "bltz0005");
        EVENT_BY_ROOM_ID.put(356, "bltz0006");

        EVENT_BY_ROOM_ID.put(359, "bltz0009");

        EVENT_BY_ROOM_ID.put(361, "mtgz1000");
        EVENT_BY_ROOM_ID.put(362, "mtgz1100");
        EVENT_BY_ROOM_ID.put(363, "zkrn0000");
        EVENT_BY_ROOM_ID.put(364, "ikai0101");
        EVENT_BY_ROOM_ID.put(365, "maca0500");
        EVENT_BY_ROOM_ID.put(366, "znkd0000");
        EVENT_BY_ROOM_ID.put(367, "znkd0900");
        EVENT_BY_ROOM_ID.put(368, "znkd1000");

        EVENT_BY_ROOM_ID.put(370, "znkd1200");
        EVENT_BY_ROOM_ID.put(371, "znkd1300");
        EVENT_BY_ROOM_ID.put(372, "lmyt0200");

        EVENT_BY_ROOM_ID.put(374, "hiku1900");
        EVENT_BY_ROOM_ID.put(375, "hiku2000");
        EVENT_BY_ROOM_ID.put(376, "znkd1400");
        EVENT_BY_ROOM_ID.put(377, "luca0800");

        EVENT_BY_ROOM_ID.put(380, "cdsp0800");
        EVENT_BY_ROOM_ID.put(381, "isho0000");
        EVENT_BY_ROOM_ID.put(382, "hiku2100");
        EVENT_BY_ROOM_ID.put(383, "mtmm0000");
        EVENT_BY_ROOM_ID.put(384, "open0100");
        EVENT_BY_ROOM_ID.put(385, "open0200");
        EVENT_BY_ROOM_ID.put(386, "endg0100");
        EVENT_BY_ROOM_ID.put(387, "endg0200");
        EVENT_BY_ROOM_ID.put(388, "endg0300");
        EVENT_BY_ROOM_ID.put(389, "znkd0001");
        EVENT_BY_ROOM_ID.put(390, "endg0400");
        EVENT_BY_ROOM_ID.put(391, "maca0500");
        EVENT_BY_ROOM_ID.put(392, "matu0000");
        EVENT_BY_ROOM_ID.put(393, "scene1");
        EVENT_BY_ROOM_ID.put(394, "scene2");
        EVENT_BY_ROOM_ID.put(395, "scene3");
        EVENT_BY_ROOM_ID.put(396, "scene4");
        EVENT_BY_ROOM_ID.put(397, "scene5");
        EVENT_BY_ROOM_ID.put(398, "scene6");
    }
}
