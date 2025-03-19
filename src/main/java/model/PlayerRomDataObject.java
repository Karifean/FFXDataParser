package model;

import atel.model.StackObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static main.DataReadingManager.DEFAULT_LOCALIZATION;
import static reading.BytesHelper.*;

/**
 * ply_rom.bin
 */
public class PlayerRomDataObject extends NameDescriptionTextObject implements Nameable, Writable {
    public static final int LENGTH = 0x2C;

    private final int[] bytes;
    
    int genreByte;
    int apReqCoefficientA;
    int apReqCoefficientB;
    int apReqCoefficientC;
    int apReqMax;

    int hpCoefficientA;
    int hpCoefficientB;
    int mpCoefficientA;
    int mpCoefficientB;
    int strCoefficientA;
    int strCoefficientB;
    int defCoefficientA;
    int defCoefficientB;
    int magCoefficientA;
    int magCoefficientB;
    int mdfCoefficientA;
    int mdfCoefficientB;
    int agiCoefficientA;
    int agiCoefficientB;
    int evaCoefficientA;
    int evaCoefficientB;
    int accCoefficientA;
    int accCoefficientB;
    int lastBytes;

    public PlayerRomDataObject(int[] bytes, int[] stringBytes, String localization) {
        super(bytes, stringBytes, localization);
        this.bytes = bytes;
        mapBytes();
        mapFlags();
    }

    public String getName(String localization) {
        return name.getLocalizedString(localization);
    }

    private void mapBytes() {
        genreByte = bytes[0x10];
        apReqCoefficientA = bytes[0x11];
        apReqCoefficientB = bytes[0x12];
        apReqCoefficientC = bytes[0x13];
        apReqMax = read4Bytes(bytes, 0x14);

        hpCoefficientA = bytes[0x18];
        hpCoefficientB = bytes[0x19];
        mpCoefficientA = bytes[0x1A];
        mpCoefficientB = bytes[0x1B];
        strCoefficientA = bytes[0x1C];
        strCoefficientB = bytes[0x1D];
        defCoefficientA = bytes[0x1E];
        defCoefficientB = bytes[0x1F];
        magCoefficientA = bytes[0x20];
        magCoefficientB = bytes[0x21];
        mdfCoefficientA = bytes[0x22];
        mdfCoefficientB = bytes[0x23];
        agiCoefficientA = bytes[0x24];
        agiCoefficientB = bytes[0x25];
        evaCoefficientA = bytes[0x26];
        evaCoefficientB = bytes[0x27];
        accCoefficientA = bytes[0x28];
        accCoefficientB = bytes[0x29];
        lastBytes = read2Bytes(bytes, 0x2A);
    }

    private void mapFlags() {
        
    }

    @Override
    public int[] toBytes(String localization) {
        int[] array = new int[PlayerRomDataObject.LENGTH];
        System.arraycopy(super.toBytes(localization), 0, array, 0, 0x10);
        array[0x10] = genreByte;
        array[0x11] = apReqCoefficientA;
        array[0x12] = apReqCoefficientB;
        array[0x13] = apReqCoefficientC;
        write4Bytes(array, 0x14, apReqMax);

        array[0x18] = hpCoefficientA;
        array[0x19] = hpCoefficientB;
        array[0x1A] = mpCoefficientA;
        array[0x1B] = mpCoefficientB;
        array[0x1C] = strCoefficientA;
        array[0x1D] = strCoefficientB;
        array[0x1E] = defCoefficientA;
        array[0x1F] = defCoefficientB;
        array[0x20] = magCoefficientA;
        array[0x21] = magCoefficientB;
        array[0x22] = mdfCoefficientA;
        array[0x23] = mdfCoefficientB;
        array[0x24] = agiCoefficientA;
        array[0x25] = agiCoefficientB;
        array[0x26] = evaCoefficientA;
        array[0x27] = evaCoefficientB;
        array[0x28] = accCoefficientA;
        array[0x29] = accCoefficientB;
        write2Bytes(array, 0x2A, lastBytes);

        return array;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add("Switch Text: " + name.getDefaultString());
        list.add("Scan Text: " + description.getDefaultString());
        list.add("Gender/Type: " + StackObject.asString(DEFAULT_LOCALIZATION, "genreBitfield", genreByte));
        List<String> apFormulaParts = new ArrayList<>();
        if (apReqCoefficientA != 0) {
            apFormulaParts.add("(Lv^3 * " + apReqCoefficientA * 0.01 + ")");
        }
        if (apReqCoefficientB != 0) {
            apFormulaParts.add("(Lv^2 * " + apReqCoefficientB * 0.1 + ")");
        }
        if (apReqCoefficientC != 0) {
            apFormulaParts.add("Lv * " + apReqCoefficientC);
        }
        if (!apFormulaParts.isEmpty()) {
            list.add("Ap Requirement Formula: " + String.join(" + ", apFormulaParts));
        }
        if (apReqMax != 0) {
            list.add("Ap Requirement Max (if Level > 100): " + apReqMax);
        }
        if (hasAeonStatGrowth()) {
            list.add(" Aeon Stat Growth");
            list.add(String.format("HP:  (Yuna's Total Stats /%3d) + Yuna's HP  * %.2f", hpCoefficientA, hpCoefficientB * 0.01));
            list.add(String.format("MP:  (Yuna's Total Stats /%3d) + Yuna's MP  * %.2f", mpCoefficientA, mpCoefficientB * 0.01));
            list.add(String.format("STR: (Yuna's Total Stats /%3d) + Yuna's STR * %.1f", strCoefficientA, strCoefficientB * 0.1));
            list.add(String.format("DEF: (Yuna's Total Stats /%3d) + Yuna's DEF * %.1f", defCoefficientA, defCoefficientB * 0.1));
            list.add(String.format("MAG: (Yuna's Total Stats /%3d) + Yuna's MAG * %.1f", magCoefficientA, magCoefficientB * 0.1));
            list.add(String.format("MDF: (Yuna's Total Stats /%3d) + Yuna's MDF * %.1f", mdfCoefficientA, mdfCoefficientB * 0.1));
            list.add(String.format("AGI: (Yuna's Total Stats /%3d) + Yuna's AGI * %.1f", agiCoefficientA, agiCoefficientB * 0.1));
            list.add(String.format("EVA: (Yuna's Total Stats /%3d) + Yuna's EVA * %.1f", evaCoefficientA, evaCoefficientB * 0.1));
            list.add(String.format("ACC: (Yuna's Total Stats /%3d) + Yuna's ACC * %.1f", accCoefficientA, accCoefficientB * 0.1));
        }
        list.add("Unknown Bitfield 2A2B: " + StackObject.asString(DEFAULT_LOCALIZATION, "bitfield", lastBytes));

        String full = list.stream().filter(s -> s != null && !s.isBlank()).collect(Collectors.joining("\n"));
        return full;
    }

    private boolean hasAeonStatGrowth() {
        return hpCoefficientA != 0 || hpCoefficientB != 0
                || mpCoefficientA != 0 || mpCoefficientB != 0
                || strCoefficientA != 0 || strCoefficientB != 0
                || defCoefficientA != 0 || defCoefficientB != 0
                || magCoefficientA != 0 || magCoefficientB != 0
                || mdfCoefficientA != 0 || mdfCoefficientB != 0
                || agiCoefficientA != 0 || agiCoefficientB != 0
                || evaCoefficientA != 0 || evaCoefficientB != 0
                || accCoefficientA != 0 || accCoefficientB != 0;
    }
}
