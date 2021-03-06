package com.pr.se.cash_manager;

import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpenseTest {
    private Expense expense;
    private static final double DELTA = 1e-15;

    @Before
    public void setUp() throws Exception {
        expense = new Expense(234.1, "23.05.2006", "Restaurant", "mums birthday");
    }
    @Test
    public void getRecurring_id() throws Exception {
        String expected = expense.getRecurring_id();
        String actual = null;
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void setRecurring_id() throws Exception {
        expense.setRecurring_id("90e446d1-c6161-4663-9123-8b97381b6d3f");
        String expected = expense.getRecurring_id();
        String actual = "90e446d1-c6161-4663-9123-8b97381b6d3f";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void getCategory() throws Exception {
        String expected = expense.getCategory();
        String actual = "Restaurant";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void setCategory() throws Exception {
        expense.setCategory("Food");
        String expected = expense.getCategory();
        String actual = "Food";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void getDescription() throws Exception {
        String expected = expense.getDescription();
        String actual = "mums birthday";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void setDescription() throws Exception {
        expense.setDescription("dads birthday");

        String expected = expense.getDescription();
        String actual = "dads birthday";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void getDate() throws Exception {
        String expected = expense.getDate();
        String actual = "23.05.2006";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void setDate() throws Exception {
        expense.setDate("06.06.1996");
        String expected = expense.getDate();
        String actual = "06.06.1996";
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void getSum() throws Exception {
        double expected = expense.getSum();
        double actual = 234.1;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void setSum() throws Exception {
        expense.setSum(4565.21);
        double expected = expense.getSum();
        double actual = 4565.21;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void addImage() throws Exception {
        Bitmap bitmap = null;
        expense.addImage(bitmap);

        int expected = 1;
        int actual = expense.getImages().size();
        assertEquals("Fields didn't match", expected, actual);
    }

    @Test
    public void deleteImage() throws Exception {
        assertTrue(false);
    }

    @Test
    public void getImages() throws Exception {
        assertTrue(false);
    }

    @Test
    public void getId() throws Exception {
        String actual = expense.getId();
        assertTrue(actual != null && !actual.equals(""));
    }

    @Test
    public void hashCodeTest() throws Exception {
        int actual = expense.hashCode();
        assertTrue(actual != 0);
    }

    @Test
    public void equalsTest() throws Exception {
        Expense expense2 = expense;
        assertTrue(expense.equals(expense2));
    }

}