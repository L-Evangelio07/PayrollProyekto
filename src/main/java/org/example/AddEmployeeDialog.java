package org.example;

import org.example.Employee;
import org.example.EmployeeTableModel;
import org.example.FireStoreConnection;
import org.example.Payslip;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddEmployeeDialog extends JDialog {
    private JTextField idNumberField, nameField, positionField,
            dailySalaryField, daysPresentField;
    private JButton saveButton, cancelButton, removeButton;
    private FireStoreConnection fireStoreConnection;
    private EmployeeTableModel model;
    private boolean isEditMode = false;
    private Employee employeeToEdit;

    public AddEmployeeDialog(JFrame parent, FireStoreConnection fireStoreConnection,
                             EmployeeTableModel model, Employee employeeToEdit) {
        this(parent, fireStoreConnection, model);
        this.employeeToEdit = employeeToEdit;
        this.isEditMode = true;
        setTitle("Edit Employee");
        populateFields();
        setupRemoveButton();
    }

    public AddEmployeeDialog(JFrame parent, FireStoreConnection fireStoreConnection,
                             EmployeeTableModel model) {
        super(parent, "Add New Employee", true);
        this.fireStoreConnection = fireStoreConnection;
        this.model = model;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridLayout(8, 2, 10, 10));
        getContentPane().setBackground(Color.WHITE);

        idNumberField = new JTextField();
        nameField = new JTextField();
        positionField = new JTextField();
        daysPresentField = new JTextField();
        dailySalaryField = new JTextField();

        saveButton = createStyledButton("Save");
        cancelButton = createStyledButton("Cancel");
        removeButton = createStyledButton("Remove");
        removeButton.setVisible(false); // Hidden by default, only shown in edit mode

        add(createLabel("ID Number:"));
        add(idNumberField);
        add(createLabel("Name:"));
        add(nameField);
        add(createLabel("Position:"));
        add(positionField);
        add(createLabel("Days Present:"));
        add(daysPresentField);
        add(createLabel("Salary:"));
        add(dailySalaryField);
        add(saveButton);
        add(cancelButton);
        add(removeButton);

        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(getParent());
    }

    private void setupRemoveButton() {
        removeButton.setVisible(true);
        removeButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove this employee?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    fireStoreConnection.deleteEmployee(employeeToEdit.getId());
                    List<Employee> employees = fireStoreConnection.getAllEmployees();
                    model.updateEmployeeList(employees);
                    dispose();
                    JOptionPane.showMessageDialog(
                            this,
                            "Employee removed successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Error removing employee: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void populateFields() {
        if (employeeToEdit != null) {
            idNumberField.setText(employeeToEdit.getId());
            nameField.setText(employeeToEdit.getName());
            positionField.setText(employeeToEdit.getPosition());
            daysPresentField.setText(String.valueOf(employeeToEdit.getDaysPresent()));
            dailySalaryField.setText(String.format("%.2f", employeeToEdit.getDailySalary()));
            idNumberField.setEditable(false);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLACK);
        return label;
    }

    private void saveEmployee() {
        if (!validateInput()) return;

        try {
            Employee employee = new Employee(
                    idNumberField.getText(),
                    nameField.getText(),
                    positionField.getText(),
                    Double.parseDouble(dailySalaryField.getText()),
                    Double.parseDouble(daysPresentField.getText())
            );

            if (isEditMode) {
                fireStoreConnection.updateEmployee(employee);
                JOptionPane.showMessageDialog(this, "Employee updated!");
            } else {
                // For new employees, also create a payslip
                Payslip payslip = new Payslip(employee);
                fireStoreConnection.addEmployee(employee, payslip);
                JOptionPane.showMessageDialog(this, "Employee added!");
            }

            // Refresh the table
            List<Employee> employees = fireStoreConnection.getAllEmployees();
            model.updateEmployeeList(employees);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    private boolean validateInput() {
        if (idNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "ID Number is required",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name is required",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (positionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Position is required",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(daysPresentField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for days present",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(dailySalaryField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid salary amount",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}