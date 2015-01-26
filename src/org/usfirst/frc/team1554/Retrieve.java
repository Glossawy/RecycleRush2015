package org.usfirst.frc.team1554;

import java.util.Hashtable;

import org.usfirst.frc.team1554.lib.util.RoboUtils;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables2.util.StringCache;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Retrieve {

	@SuppressWarnings("rawtypes")
	public static void retrieveSmartDashboardKeys(){
		NetworkTable table = RoboUtils.getStaticField(SmartDashboard.class, "table", NetworkTable.class);
		StringCache tableCache = RoboUtils.getInstanceField(NetworkTable.class, table, "absoluteKeyCache", StringCache.class);
		Hashtable cache = RoboUtils.getInstanceField(StringCache.class, tableCache, "cache", Hashtable.class);
		
		for(Object o : cache.keySet()) {
			System.out.println(String.valueOf(o));
		}
	}
	
}
