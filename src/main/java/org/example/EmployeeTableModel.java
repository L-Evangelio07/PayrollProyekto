package org.example;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTableModel extends AbstractTableModel {
    private final String[] columnNames = {

            "ID", "Name", "Position", "Daily Rate", "Days Present", "Days Absent"

    };

    private List<Employee> employees;


    public EmployeeTableModel() {
        this.employees = new ArrayList<>();
        this.payslips = new ArrayList<>();

    }

    @Override
    public int getRowCount() {
        return employees.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Employee emp = employees.get(rowIndex);
        switch (columnIndex) {
            case 0: return emp.getId();
            case 1: return emp.getName();
            case 2: return emp.getPosition();
            case 3: return emp.getDailySalary();
            case 4: return emp.getDaysPresent();
//            case 5: return slip.getGrossSalary();
//            case 6: return slip.getPagIbig();
//            case 7: return slip.getPhilHealth();
//            case 8: return slip.getSss();
//            case 9: return slip.getIncomeTax();
//            case 10: return slip.getTotalDeductions();
//            case 11: return slip.getNetPay();

            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }


    public void addEmployee(Employee employee){
        employees.add(employee);
        fireTableDataChanged();
    }
}


    public void updateEmployeeList(List<Employee> employees) {
        this.employees = employees;
        fireTableDataChanged();
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}