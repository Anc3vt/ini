package ru.ancevt.util.ini;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Needs etalon files:
 * test.ini
 * test-merge-with.ini
 * 
 * Encoding: UTF-8
 * @author ancevt
 *
 */
public class TestIni {
	
	private static String iniSource, iniSourceMergeWith;
	
	private Ini ini;
	
	@BeforeClass
	public static void beforeClass() throws IOException {
		final File file = new File(
			TestIni.class.getClassLoader()
			.getResource("test.ini")
			.getFile()
		);
		
		final byte[] iniBytes = Files.readAllBytes(file.toPath());
		iniSource = new String(iniBytes);
		
		final File file1 = new File(
			TestIni.class.getClassLoader()
			.getResource("test-merge-with.ini")
			.getFile()
		);
		final byte[] iniBytes1 = Files.readAllBytes(file1.toPath());
		iniSourceMergeWith = new String(iniBytes1);
	}
	
	@AfterClass
	public static void afterClass() {
		
	}
	
	@Before
	public void beforeTest() {
		ini = new Ini(iniSource);
	}
	
	@After
	public void afterTest() {
		ini = null;
	}
	
	@Test
	public void sectionCount() {
		assertEquals(2, ini.getSectionCount());
	}
	
	@Test
	public void globalSectionEffectiveSize() {
		assertEquals(5, ini.getGlobalSection().effectiveSize());
	}
	
	@Test
	public void globalSectionSize() {
		assertEquals(9, ini.getGlobalSection().size());
	}
	
	@Test
	public void testBoolean() {
		assertTrue(ini.getBoolean(null, "boolean", false));
	}
	
	@Test
	public void testInvalidBoolean() {
		assertFalse(ini.getBoolean(null, "timeout", false));
	}
	
	@Test
	public void hasSection() {
		assertTrue(ini.hasSection("Section1"));
	}
	
	@Test
	public void sectionEffectiveSize() {
		assertEquals(2, ini.getSection("Section1").effectiveSize());
	}
	
	@Test
	public void sectionSize() {
		assertEquals(4, ini.getSection("Section1").size());
	}
	
	@Test
	public void addLine() {
		ini.addLine("addedKey", "addedValue");
		assertEquals(ini.getString(null, "addedKey"), "addedValue");
	}
	
	@Test 
	public void clear() {
		ini.clear();
		assertEquals(0, ini.getSectionCount());
	}
	
	@Test
	public void clearEmpty() {
		ini.clearEmpty();
		assertEquals(3, ini.getSection("Section1").size());
	}
	
	@Test
	public void clearComment() {
		ini.clearComment();
		assertEquals(3, ini.getSection("Section1").size());
	}
	
	@Test
	public void removeSections() {
		ini.removeSection("Section1");
		assertEquals(1, ini.getSectionCount());
	}
	
	@Test
	public void valueConsistence() {
		final String value = ini.getString("Section2", "key1FromSection2");
		assertEquals(" This is value from section 2", value);
	}
	
	@Test
	public void integerValidity() {
		assertEquals(2500, ini.getInt(null, "timeout")/2);
	}
	
	@Test(expected=NumberFormatException.class)
	public void stringAsDecimal() {
		assertEquals(0f, ini.getFloat(null, "host"), 0f);
	}
	
	@Test
	public void merge() {
		final Ini iniMergeWith = new Ini(iniSourceMergeWith);
		ini.merge(iniMergeWith);
		assertTrue(
			123 == ini.getInt(null, "forMerge") &&
			321 == ini.getInt("Section2", "forMerge") &&
			148 == ini.getInt("Section3", "forMerge") &&
			7777 == ini.getInt(null, "port") &&
			"Кириллическое значение".equals(ini.getString("Section2", "cyrillic"))
		);
	}
	
	@Test
	public void ignoreCaseLineAccess() {
		ini.setIgnoreCase(true);
		assertEquals(5000, ini.getInt(null, "TIMEOUT"));
	}
	
	@Test 
	public void ignoreCaseLineRemoving() {
		ini.setIgnoreCase(true);
		ini.removeLine("TIMEOUT");
		assertEquals(4, ini.getGlobalSection().effectiveSize());
	}
	
	@Test
	public void ignoreCaseSectionAccess() {
		ini.setIgnoreCase(true);
		assertNotNull(ini.getSection("SECTION1"));
	}
	
	@Test
	public void ignoreCaseSectionRemoving() {
		ini.setIgnoreCase(true);
		ini.removeSection("SECTION1");
		assertEquals(1, ini.getSectionCount());
	}
	
	
}







