package com.castelcode.cruisecompanion.expenses_tests;

import com.castelcode.cruisecompanion.expenses.Expense;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpensesTest {
    @Test
    public void Expense_creation() throws Exception {
        Expense expense = new Expense("description", 12.0);
        assertEquals("description", expense.getDescription());
        assertEquals(12.0, expense.getCost(), 0);
    }
}
