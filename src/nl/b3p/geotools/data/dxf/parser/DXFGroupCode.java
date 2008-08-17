package nl.b3p.geotools.data.dxf.parser;

import nl.b3p.geotools.data.dxf.parser.DXFValueType;

/** 
 * An enumeration that represents all valid DXF Group Codes with
 * associated value types as listed in specs.
 * 
 */
public enum DXFGroupCode {

    TYPE(0, DXFValueType.STRING),
    TEXT(1, DXFValueType.STRING),
    NAME(2, DXFValueType.STRING),
    TEXT_OR_NAME_2(3, DXFValueType.STRING),
    TEXT_OR_NAME_3(4, DXFValueType.STRING),
    HANDLE(5, DXFValueType.STRING),
    LINETYPE_NAME(6, DXFValueType.STRING),
    TEXT_STYLE_NAME(7, DXFValueType.STRING),
    LAYER_NAME(8, DXFValueType.STRING),
    VARIABLE_NAME(9, DXFValueType.STRING),
    X_1(10, DXFValueType.DOUBLE),
    X_2(11, DXFValueType.DOUBLE),
    X_3(12, DXFValueType.DOUBLE),
    X_4(13, DXFValueType.DOUBLE),
    X_5(14, DXFValueType.DOUBLE),
    X_6(15, DXFValueType.DOUBLE),
    X_7(16, DXFValueType.DOUBLE),
    X_8(17, DXFValueType.DOUBLE),
    X_9(18, DXFValueType.DOUBLE),
    Y_1(20, DXFValueType.DOUBLE),
    Y_2(21, DXFValueType.DOUBLE),
    Y_3(22, DXFValueType.DOUBLE),
    Y_4(23, DXFValueType.DOUBLE),
    Y_5(24, DXFValueType.DOUBLE),
    Y_6(25, DXFValueType.DOUBLE),
    Y_7(26, DXFValueType.DOUBLE),
    Y_8(27, DXFValueType.DOUBLE),
    Y_9(28, DXFValueType.DOUBLE),
    Z_1(30, DXFValueType.DOUBLE),
    Z_2(31, DXFValueType.DOUBLE),
    Z_3(32, DXFValueType.DOUBLE),
    Z_4(33, DXFValueType.DOUBLE),
    Z_5(34, DXFValueType.DOUBLE),
    Z_6(35, DXFValueType.DOUBLE),
    Z_7(36, DXFValueType.DOUBLE),
    Z_8(37, DXFValueType.DOUBLE),
    ELEVATION(38, DXFValueType.DOUBLE),
    THICKNESS(39, DXFValueType.DOUBLE),
    DOUBLE_1(40, DXFValueType.DOUBLE),
    DOUBLE_2(41, DXFValueType.DOUBLE),
    DOUBLE_3(42, DXFValueType.DOUBLE),
    DOUBLE_4(43, DXFValueType.DOUBLE),
    DOUBLE_5(44, DXFValueType.DOUBLE),
    DOUBLE_6(45, DXFValueType.DOUBLE),
    DOUBLE_7(46, DXFValueType.DOUBLE),
    DOUBLE_8(47, DXFValueType.DOUBLE),
    LINETYPE_SCALE(48, DXFValueType.DOUBLE),
    REPEATED_DOUBLE_VALUE(49, DXFValueType.DOUBLE),
    ANGLE_1(50, DXFValueType.DOUBLE),
    ANGLE_2(51, DXFValueType.DOUBLE),
    ANGLE_3(52, DXFValueType.DOUBLE),
    ANGLE_4(53, DXFValueType.DOUBLE),
    ANGLE_5(54, DXFValueType.DOUBLE),
    ANGLE_6(55, DXFValueType.DOUBLE),
    ANGLE_7(56, DXFValueType.DOUBLE),
    ANGLE_8(57, DXFValueType.DOUBLE),
    ANGLE_9(58, DXFValueType.DOUBLE),
    VISIBILITY(60, DXFValueType.SHORT),
    COLOR(62, DXFValueType.SHORT),
    ENTITIES_FOLLOW_FLAG(66, DXFValueType.SHORT),
    MODEL_OR_PAPER_SPACE(67, DXFValueType.SHORT),
    VIEWPORT_DATA_1(68, DXFValueType.SHORT),
    VIEWPORT_DATA_2(69, DXFValueType.SHORT),
    INT_1(70, DXFValueType.SHORT),
    INT_2(71, DXFValueType.SHORT),
    INT_3(72, DXFValueType.SHORT),
    INT_4(73, DXFValueType.SHORT),
    INT_5(74, DXFValueType.SHORT),
    INT_6(75, DXFValueType.SHORT),
    INT_7(76, DXFValueType.SHORT),
    INT_8(77, DXFValueType.SHORT),
    INT_9(78, DXFValueType.SHORT),
    INT_10(79, DXFValueType.SHORT),
    INT32_1(90, DXFValueType.INTEGER),
    INT32_2(91, DXFValueType.INTEGER),
    INT32_3(92, DXFValueType.INTEGER),
    INT32_4(93, DXFValueType.INTEGER),
    INT32_5(94, DXFValueType.INTEGER),
    INT32_6(95, DXFValueType.INTEGER),
    INT32_7(96, DXFValueType.INTEGER),
    INT32_8(97, DXFValueType.INTEGER),
    INT32_9(98, DXFValueType.INTEGER),
    INT32_10(99, DXFValueType.INTEGER),
    SUBCLASS_DATA_MARKER(100, DXFValueType.STRING),
    GROUP_CONTROL_STRING(102, DXFValueType.STRING),
    DIMVAR_OBJECT_HANDLE(105, DXFValueType.STRING),
    UCS_ORIGIN_X(110, DXFValueType.DOUBLE),
    UCS_X_AXIS_X(111, DXFValueType.DOUBLE),
    UCS_Y_AXIS_X(112, DXFValueType.DOUBLE),
    UCS_ORIGIN_Y(120, DXFValueType.DOUBLE),
    UCS_X_AXIS_Y(121, DXFValueType.DOUBLE),
    UCS_Y_AXIS_Y(122, DXFValueType.DOUBLE),
    UCS_ORIGIN_Z(130, DXFValueType.DOUBLE),
    UCS_X_AXIS_Z(131, DXFValueType.DOUBLE),
    UCS_Y_AXIS_Z(132, DXFValueType.DOUBLE),
    DOUBLE_10(140, DXFValueType.DOUBLE),
    DOUBLE_11(141, DXFValueType.DOUBLE),
    DOUBLE_12(142, DXFValueType.DOUBLE),
    DOUBLE_13(143, DXFValueType.DOUBLE),
    DOUBLE_14(144, DXFValueType.DOUBLE),
    DOUBLE_15(145, DXFValueType.DOUBLE),
    DOUBLE_16(146, DXFValueType.DOUBLE),
    DOUBLE_17(147, DXFValueType.DOUBLE),
    DOUBLE_18(148, DXFValueType.DOUBLE),
    DOUBLE_19(149, DXFValueType.DOUBLE),
    INT16_1(170, DXFValueType.SHORT),
    INT16_2(171, DXFValueType.SHORT),
    INT16_3(172, DXFValueType.SHORT),
    INT16_4(173, DXFValueType.SHORT),
    INT16_5(174, DXFValueType.SHORT),
    INT16_6(175, DXFValueType.SHORT),
    INT16_7(176, DXFValueType.SHORT),
    INT16_8(177, DXFValueType.SHORT),
    INT16_9(178, DXFValueType.SHORT),
    INT16_10(179, DXFValueType.SHORT),
    EXTRUSION_X(210, DXFValueType.DOUBLE),
    EXTRUSION_Y(220, DXFValueType.DOUBLE),
    EXTRUSION_Z(230, DXFValueType.DOUBLE),
    INT16_11(270, DXFValueType.SHORT),
    INT16_12(271, DXFValueType.SHORT),
    INT16_13(272, DXFValueType.SHORT),
    INT16_14(273, DXFValueType.SHORT),
    INT16_15(274, DXFValueType.SHORT),
    INT16_16(275, DXFValueType.SHORT),
    INT16_17(276, DXFValueType.SHORT),
    INT16_18(277, DXFValueType.SHORT),
    INT16_19(278, DXFValueType.SHORT),
    INT16_20(279, DXFValueType.SHORT),
    INT16_21(280, DXFValueType.SHORT),
    INT16_22(281, DXFValueType.SHORT),
    INT16_23(282, DXFValueType.SHORT),
    INT16_24(283, DXFValueType.SHORT),
    INT16_25(284, DXFValueType.SHORT),
    INT16_26(285, DXFValueType.SHORT),
    INT16_27(286, DXFValueType.SHORT),
    INT16_28(287, DXFValueType.SHORT),
    INT16_29(288, DXFValueType.SHORT),
    INT16_30(289, DXFValueType.SHORT),
    BOOLEAN_1(290, DXFValueType.BOOLEAN),
    BOOLEAN_2(291, DXFValueType.BOOLEAN),
    BOOLEAN_3(292, DXFValueType.BOOLEAN),
    BOOLEAN_4(293, DXFValueType.BOOLEAN),
    BOOLEAN_5(294, DXFValueType.BOOLEAN),
    BOOLEAN_6(295, DXFValueType.BOOLEAN),
    BOOLEAN_7(296, DXFValueType.BOOLEAN),
    BOOLEAN_8(297, DXFValueType.BOOLEAN),
    BOOLEAN_9(298, DXFValueType.BOOLEAN),
    BOOLEAN_10(299, DXFValueType.BOOLEAN),
    ARBITRARY_TEXT_1(300, DXFValueType.STRING),
    ARBITRARY_TEXT_2(301, DXFValueType.STRING),
    ARBITRARY_TEXT_3(302, DXFValueType.STRING),
    ARBITRARY_TEXT_4(303, DXFValueType.STRING),
    ARBITRARY_TEXT_5(304, DXFValueType.STRING),
    ARBITRARY_TEXT_6(305, DXFValueType.STRING),
    ARBITRARY_TEXT_7(306, DXFValueType.STRING),
    ARBITRARY_TEXT_8(307, DXFValueType.STRING),
    ARBITRARY_TEXT_9(308, DXFValueType.STRING),
    ARBITRARY_TEXT_10(309, DXFValueType.STRING),
    ARBITRARY_BINARY_CHUNK_1(310, DXFValueType.BINHEX),
    ARBITRARY_BINARY_CHUNK_2(311, DXFValueType.BINHEX),
    ARBITRARY_BINARY_CHUNK_3(312, DXFValueType.BINHEX),
    ARBITRARY_BINARY_CHUNK_4(313, DXFValueType.BINHEX),
    ARBITRARY_BINARY_CHUNK_5(314, DXFValueType.BINHEX),
    ARBITRARY_BINARY_CHUNK_6(315, DXFValueType.BINHEX),
    ARBITRARY_BINARY_CHUNK_7(316, DXFValueType.BINHEX),
    ARBITRARY_BINARY_CHUNK_8(317, DXFValueType.BINHEX),
    ARBITRARY_BINARY_CHUNK_9(318, DXFValueType.BINHEX),
    ARBITRARY_BINARY_CHUNK_10(319, DXFValueType.BINHEX),
    ARBITRARY_OBJECT_HANDLE_1(320, DXFValueType.HANDLEHEX),
    ARBITRARY_OBJECT_HANDLE_2(321, DXFValueType.HANDLEHEX),
    ARBITRARY_OBJECT_HANDLE_3(322, DXFValueType.HANDLEHEX),
    ARBITRARY_OBJECT_HANDLE_4(323, DXFValueType.HANDLEHEX),
    ARBITRARY_OBJECT_HANDLE_5(324, DXFValueType.HANDLEHEX),
    ARBITRARY_OBJECT_HANDLE_6(325, DXFValueType.HANDLEHEX),
    ARBITRARY_OBJECT_HANDLE_7(326, DXFValueType.HANDLEHEX),
    ARBITRARY_OBJECT_HANDLE_8(327, DXFValueType.HANDLEHEX),
    ARBITRARY_OBJECT_HANDLE_9(328, DXFValueType.HANDLEHEX),
    ARBITRARY_OBJECT_HANDLE_10(329, DXFValueType.HANDLEHEX),
    SOFT_POINTER_HANDLE_1(330, DXFValueType.IDHEX),
    SOFT_POINTER_HANDLE_2(331, DXFValueType.IDHEX),
    SOFT_POINTER_HANDLE_3(332, DXFValueType.IDHEX),
    SOFT_POINTER_HANDLE_4(333, DXFValueType.IDHEX),
    SOFT_POINTER_HANDLE_5(334, DXFValueType.IDHEX),
    SOFT_POINTER_HANDLE_6(335, DXFValueType.IDHEX),
    SOFT_POINTER_HANDLE_7(336, DXFValueType.IDHEX),
    SOFT_POINTER_HANDLE_8(337, DXFValueType.IDHEX),
    SOFT_POINTER_HANDLE_9(338, DXFValueType.IDHEX),
    SOFT_POINTER_HANDLE_10(339, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_1(340, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_2(341, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_3(342, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_4(343, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_5(344, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_6(345, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_7(346, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_8(347, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_9(348, DXFValueType.IDHEX),
    HARD_POINTER_HANDLE_10(349, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_1(350, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_2(351, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_3(352, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_4(353, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_5(354, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_6(355, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_7(356, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_8(357, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_9(358, DXFValueType.IDHEX),
    SOFT_OWNER_HANDLE_10(359, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_1(360, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_2(361, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_3(362, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_4(363, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_5(364, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_6(365, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_7(366, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_8(367, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_9(368, DXFValueType.IDHEX),
    HARD_OWNER_HANDLE_10(369, DXFValueType.IDHEX),
    LINEWEIGHT_1(370, DXFValueType.SHORT),
    LINEWEIGHT_2(371, DXFValueType.SHORT),
    LINEWEIGHT_3(372, DXFValueType.SHORT),
    LINEWEIGHT_4(373, DXFValueType.SHORT),
    LINEWEIGHT_5(374, DXFValueType.SHORT),
    LINEWEIGHT_6(375, DXFValueType.SHORT),
    LINEWEIGHT_7(376, DXFValueType.SHORT),
    LINEWEIGHT_8(377, DXFValueType.SHORT),
    LINEWEIGHT_9(378, DXFValueType.SHORT),
    LINEWEIGHT_10(379, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_1(380, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_2(381, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_3(382, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_4(383, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_5(384, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_6(385, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_7(386, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_8(387, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_9(388, DXFValueType.SHORT),
    PLOTSTYLE_NAME_TYPE_10(389, DXFValueType.SHORT),
    PLOTSTYLE_NAME_HANDLE_1(390, DXFValueType.IDHEX),
    PLOTSTYLE_NAME_HANDLE_2(391, DXFValueType.IDHEX),
    PLOTSTYLE_NAME_HANDLE_3(392, DXFValueType.IDHEX),
    PLOTSTYLE_NAME_HANDLE_4(393, DXFValueType.IDHEX),
    PLOTSTYLE_NAME_HANDLE_5(394, DXFValueType.IDHEX),
    PLOTSTYLE_NAME_HANDLE_6(395, DXFValueType.IDHEX),
    PLOTSTYLE_NAME_HANDLE_7(396, DXFValueType.IDHEX),
    PLOTSTYLE_NAME_HANDLE_8(397, DXFValueType.IDHEX),
    PLOTSTYLE_NAME_HANDLE_9(398, DXFValueType.IDHEX),
    PLOTSTYLE_NAME_HANDLE_10(399, DXFValueType.IDHEX),
    INT16_31(400, DXFValueType.SHORT),
    INT16_32(401, DXFValueType.SHORT),
    INT16_33(402, DXFValueType.SHORT),
    INT16_34(403, DXFValueType.SHORT),
    INT16_35(404, DXFValueType.SHORT),
    INT16_36(405, DXFValueType.SHORT),
    INT16_37(406, DXFValueType.SHORT),
    INT16_38(407, DXFValueType.SHORT),
    INT16_39(408, DXFValueType.SHORT),
    INT16_40(409, DXFValueType.SHORT),
    STRING_1(410, DXFValueType.STRING),
    STRING_2(411, DXFValueType.STRING),
    STRING_3(412, DXFValueType.STRING),
    STRING_4(413, DXFValueType.STRING),
    STRING_5(414, DXFValueType.STRING),
    STRING_6(415, DXFValueType.STRING),
    STRING_7(416, DXFValueType.STRING),
    STRING_8(417, DXFValueType.STRING),
    STRING_9(418, DXFValueType.STRING),
    STRING_10(419, DXFValueType.STRING),
    INT32_11(420, DXFValueType.INTEGER),
    INT32_12(421, DXFValueType.INTEGER),
    INT32_13(422, DXFValueType.INTEGER),
    INT32_14(423, DXFValueType.INTEGER),
    INT32_15(424, DXFValueType.INTEGER),
    INT32_16(425, DXFValueType.INTEGER),
    INT32_17(426, DXFValueType.INTEGER),
    INT32_18(427, DXFValueType.INTEGER),
    STRING_11(430, DXFValueType.STRING),
    STRING_12(431, DXFValueType.STRING),
    STRING_13(432, DXFValueType.STRING),
    STRING_14(433, DXFValueType.STRING),
    STRING_15(434, DXFValueType.STRING),
    STRING_16(435, DXFValueType.STRING),
    STRING_17(436, DXFValueType.STRING),
    STRING_18(437, DXFValueType.STRING),
    INT32_19(440, DXFValueType.INTEGER),
    INT32_20(441, DXFValueType.INTEGER),
    INT32_21(442, DXFValueType.INTEGER),
    INT32_22(443, DXFValueType.INTEGER),
    INT32_23(444, DXFValueType.INTEGER),
    INT32_24(445, DXFValueType.INTEGER),
    INT32_25(446, DXFValueType.INTEGER),
    INT32_26(447, DXFValueType.INTEGER),
    LONG_1(450, DXFValueType.LONG),
    LONG_2(451, DXFValueType.LONG),
    LONG_3(452, DXFValueType.LONG),
    LONG_4(453, DXFValueType.LONG),
    LONG_5(454, DXFValueType.LONG),
    LONG_6(455, DXFValueType.LONG),
    LONG_7(456, DXFValueType.LONG),
    LONG_8(457, DXFValueType.LONG),
    LONG_9(458, DXFValueType.LONG),
    LONG_10(459, DXFValueType.LONG),
    DOUBLE_20(460, DXFValueType.DOUBLE),
    DOUBLE_21(461, DXFValueType.DOUBLE),
    DOUBLE_22(462, DXFValueType.DOUBLE),
    DOUBLE_23(463, DXFValueType.DOUBLE),
    DOUBLE_24(464, DXFValueType.DOUBLE),
    DOUBLE_25(465, DXFValueType.DOUBLE),
    DOUBLE_26(466, DXFValueType.DOUBLE),
    DOUBLE_27(467, DXFValueType.DOUBLE),
    DOUBLE_28(468, DXFValueType.DOUBLE),
    DOUBLE_29(469, DXFValueType.DOUBLE),
    STRING_19(470, DXFValueType.STRING),
    STRING_20(471, DXFValueType.STRING),
    STRING_21(472, DXFValueType.STRING),
    STRING_22(473, DXFValueType.STRING),
    STRING_23(474, DXFValueType.STRING),
    STRING_24(475, DXFValueType.STRING),
    STRING_25(476, DXFValueType.STRING),
    STRING_26(477, DXFValueType.STRING),
    STRING_27(478, DXFValueType.STRING),
    STRING_28(479, DXFValueType.STRING),
    COMMENT(999, DXFValueType.STRING),
    XDATA_ASCII_STRING(1000, DXFValueType.STRING),
    XDATA_APPLICATION_NAME(1001, DXFValueType.STRING),
    XDATA_CONTROL_STRING(1002, DXFValueType.STRING),
    XDATA_LAYER_NAME(1003, DXFValueType.STRING),
    XDATA_CHUNK_OF_BYTES(1004, DXFValueType.STRING),
    XDATA_ENTITY_HANDLE(1005, DXFValueType.STRING),
    XDATA_X_1(1010, DXFValueType.DOUBLE),
    XDATA_X_2(1011, DXFValueType.DOUBLE),
    XDATA_X_3(1012, DXFValueType.DOUBLE),
    XDATA_X_4(1013, DXFValueType.DOUBLE),
    XDATA_Y_1(1020, DXFValueType.DOUBLE),
    XDATA_Y_2(1021, DXFValueType.DOUBLE),
    XDATA_Y_3(1022, DXFValueType.DOUBLE),
    XDATA_Y_4(1023, DXFValueType.DOUBLE),
    XDATA_Z_1(1030, DXFValueType.DOUBLE),
    XDATA_Z_2(1031, DXFValueType.DOUBLE),
    XDATA_Z_3(1032, DXFValueType.DOUBLE),
    XDATA_Z_4(1033, DXFValueType.DOUBLE),
    XDATA_DOUBLE(1040, DXFValueType.DOUBLE),
    XDATA_DISTANCE(1041, DXFValueType.DOUBLE),
    XDATA_SCALE_FACTOR(1042, DXFValueType.DOUBLE),
    XDATA_INT16(1070, DXFValueType.SHORT),
    XDATA_INT32(1071, DXFValueType.INTEGER);
    private int m_code;
    private DXFValueType m_type;

    DXFGroupCode(int code, DXFValueType type) {
        m_code = code;
        m_type = type;
    }

    public int toInt() {
        return m_code;
    }

    public DXFValueType toType() {
        return m_type;
    }

    public static DXFGroupCode getGroupCode(int code) {
        for (DXFGroupCode agc : DXFGroupCode.values()) {
            if (agc.toInt() == code) {
                return agc;
            }
        }
        throw new Error("Unknown Group Code: "+ code + ", should not happen, parse error?");
    }
}
