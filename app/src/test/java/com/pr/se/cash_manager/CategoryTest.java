package com.pr.se.cash_manager;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CategoryTest {
    private Category category;
    private static final double DELTA = 1e-15;

    @Before
    public void setUp() throws Exception {
        category = new Category("Restaurant", 0, true);
    }

    @Test
    public void CatecoryConstructor() throws Exception {
        Category c = new Category("asdf", -1, false);
        double expected = 0;
        double actual = c.getLimit();
        assertEquals(expected, actual, DELTA);

        expected = 0;
        actual = c.getSum();
        assertEquals(expected, actual, DELTA);

        assertTrue(c.getSubCategories() != null);
    }

    @Test
    public void addSubCategory() throws Exception {
        Category subcategory = new Category("Food", 0, false);
        assertTrue(category.addSubCategory(subcategory) == true);
        List<Category> expected = new ArrayList<>();
        expected.add(subcategory);
        List<Category> actual = category.getSubCategories();
        assertTrue(expected.size() == 1);
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void addSubCategory_null() throws Exception {
        assertTrue(category.addSubCategory(null) == false);
        List<Category> expected = new ArrayList<>();
        List<Category> actual = category.getSubCategories();
        assertTrue(expected.size() == 0);
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void getSubCategories() throws Exception {
        List<Category> expected = new ArrayList<>();
        List<Category> actual = category.getSubCategories();
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void setCategories() throws Exception {
        Category subcategory = new Category("Food", 0, false);
        ArrayList<Category> subcategories = new ArrayList<>();
        subcategories.add(subcategory);

        category.setCategories(subcategories);

        List<Category> expected = new ArrayList<>();
        expected.add(subcategory);
        List<Category> actual = category.getSubCategories();
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void addSum() throws Exception {
        category.addSum(5463.43);
        double expected = 5463.43;
        double actual = category.getSum();
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void zeroSum() throws Exception {
        double expected = 0;
        double actual = 0;
        category.zeroSum();
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void getSum() throws Exception {
        double expected = 0;
        double actual = category.getSum();
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void getName() throws Exception {
        String expected = category.getName();
        String actual = "Restaurant";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void setName() throws NoSuchFieldException, IllegalAccessException  {
        category.setName("foo");
        String expected = category.getName();
        String actual = "foo";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void getLimit() throws Exception {
        double expected = 0;
        double actual = category.getLimit();
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void setLimit() throws Exception {
        category.setLimit(435.72);
        double expected = 435.72;
        double actual = category.getLimit();
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void getId() throws Exception {
        String actual = category.getId();
        assertTrue(actual != null && !actual.equals(""));
    }

    @Test
    public void toString_Test() throws Exception {
        String expected = category.toString();
        String actual = "Restaurant";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void sortSubCategories() throws Exception {
        Category subcategory1 = new Category("Others", 0, false);
        Category subcategory2 = new Category("Food", 0, false);
        category.addSubCategory(subcategory1);
        category.addSubCategory(subcategory2);

        List<Category> expected = new ArrayList<>();
        expected.add(subcategory2);
        expected.add(subcategory1);

        List<Category> actual = category.getSubCategories();

        assertEquals("Fields didn't match", expected.get(0), actual.get(0));
        assertEquals("Fields didn't match", expected.get(1), actual.get(1));
    }
}