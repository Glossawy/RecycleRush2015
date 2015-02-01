package org.usfirst.frc.team1554;

import java.util.Hashtable;

import org.usfirst.frc.team1554.lib.util.RoboUtils;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables2.AbstractNetworkTableEntryStore;
import edu.wpi.first.wpilibj.networktables2.NetworkTableNode;
import edu.wpi.first.wpilibj.networktables2.util.StringCache;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Retrieve {

	@SuppressWarnings("rawtypes")
	public static void retrieveSmartDashboardKeys() {
		// Check the Key Cache
		final NetworkTable table = RoboUtils.getStaticField(SmartDashboard.class, "table", NetworkTable.class);
		final StringCache tableCache = RoboUtils.getInstanceField(NetworkTable.class, table.getSubTable("DB"), "absoluteKeyCache", StringCache.class);
		final Hashtable cache = RoboUtils.getInstanceField(StringCache.class, tableCache, "cache", Hashtable.class);

		System.out.println("Attempting cache read...");
		for (final Object o : cache.keySet()) {
			System.out.println(String.valueOf(o));
		}

		// Check the Entry Store
		final NetworkTableNode node = RoboUtils.getInstanceField(NetworkTable.class, table, "node", NetworkTableNode.class);
		final AbstractNetworkTableEntryStore store = RoboUtils.getInstanceField(NetworkTableNode.class, node, "entryStore", AbstractNetworkTableEntryStore.class);
		final Hashtable storeCache = RoboUtils.getInstanceField(AbstractNetworkTableEntryStore.class, store, "namedEntries", Hashtable.class);

		System.out.println("Attempt store read...");
		for (final Object o : storeCache.keySet()) {
			System.out.println(String.valueOf(o));
		}
	}

}
