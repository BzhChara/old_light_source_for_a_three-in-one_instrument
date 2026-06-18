package com.whswzz.prfluroanalyzer.utils;

import android.text.TextUtils;

public class LimitUnitUtil {
	public static final String UNIT_MG_KG = "mg/kg(ppm)";
	public static final String UNIT_UG_KG = "ug/kg(ppb)";
	public static final String[] LIMIT_UNITS = { UNIT_MG_KG, UNIT_UG_KG };

	public static String normalizeUnit(String unit) {
		if (TextUtils.isEmpty(unit)) {
			return UNIT_MG_KG;
		}
		if (UNIT_UG_KG.equals(unit)) {
			return UNIT_UG_KG;
		}
		return UNIT_MG_KG;
	}

	public static boolean isUgKg(String unit) {
		return UNIT_UG_KG.equals(normalizeUnit(unit));
	}

	public static double toMgKg(double value, String unit) {
		if (isUgKg(unit)) {
			return value / 1000;
		}
		return value;
	}

	public static double fromMgKg(double value, String unit) {
		if (isUgKg(unit)) {
			return value * 1000;
		}
		return value;
	}

	public static String formatValue(double value, String unit) {
		return String.format("%.3f", fromMgKg(value, unit));
	}

	public static String formatLimit(double value, String unit) {
		String normalizedUnit = normalizeUnit(unit);
		return formatValue(value, normalizedUnit) + " " + normalizedUnit;
	}
}
