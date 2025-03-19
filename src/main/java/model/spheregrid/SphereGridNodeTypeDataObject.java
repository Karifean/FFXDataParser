package model.spheregrid;

import atel.model.StackObject;
import main.DataAccess;
import main.StringHelper;
import model.*;
import reading.BytesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static reading.BytesHelper.write2Bytes;

/**
 * panel.bin
 * referenced in dat09/10/11.dat
 */
public class SphereGridNodeTypeDataObject extends NameDescriptionTextObject implements Nameable, Writable {
    public static final int LENGTH = 0x18;
    private final int[] bytes;

    private int nodeEffectBitfield; // 0080 = Lock
    private int learnedMove;
    private int increaseAmount;
    private int appearanceType;

    public SphereGridNodeTypeDataObject(int[] bytes, int[] stringBytes, String localization) {
        super(bytes, stringBytes, localization);
        this.bytes = bytes;
        mapBytes();
    }

    private void mapBytes() {
        nodeEffectBitfield = read2Bytes(0x10);
        learnedMove = read2Bytes(0x12);
        increaseAmount = read2Bytes(0x14);
        appearanceType = read2Bytes(0x16);
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[SphereGridNodeTypeDataObject.LENGTH];
        System.arraycopy(super.toBytes(localization), 0, array, 0, 0x10);
        write2Bytes(array, 0x10, nodeEffectBitfield);
        write2Bytes(array, 0x12, learnedMove);
        write2Bytes(array, 0x14, increaseAmount);
        write2Bytes(array, 0x16, appearanceType);
        return array;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Effects: " + StackObject.bitfieldToString("sgNodeEffectsBitfield", nodeEffectBitfield) + StringHelper.hex4Suffix(nodeEffectBitfield));
        if (learnedMove > 0) {
            list.add("Teaches Move: " + asMove(learnedMove));
        }
        if (increaseAmount > 0) {
            list.add("Increase=" + StringHelper.hex4WithSuffix(increaseAmount));
        }
        list.add("Appearance?=" + StringHelper.hex4WithSuffix(appearanceType));
        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(", "));
        String descriptionStr = description.getDefaultString();
        return String.format("%-20s", getName()) + " { " + full + " } " + descriptionStr;
    }

    private int read2Bytes(int offset) {
        return BytesHelper.read2Bytes(bytes, offset);
    }

    private static String asMove(int idx) {
        CommandDataObject move = DataAccess.getCommand(idx);
        return (move != null ? move.name : "null") + StringHelper.hex4Suffix(idx);
    }
}
