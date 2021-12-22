package Client;

import javax.swing.*;
import java.awt.*;

public class SizeDialog extends JDialog {
    private JTextField sizeField;

    private JButton btnOk;
    private JButton btnCancel;

    public SizeDialog(Frame parent,GameOfLife gl) {
        super(parent, "Set size", true);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);


        JLabel sizeLabel = new JLabel("Size:");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(sizeLabel, gbc);
        sizeField = new JTextField();

        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(sizeField, gbc);

        btnOk = new JButton("Resize");
        btnOk.addActionListener(e -> {
            gl.changeSize(Integer.parseInt(sizeField.getText()));
            setVisible(false);
        });
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(btnOk, gbc);
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> {
            setVisible(false);
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnCancel, gbc);
        getContentPane().add(panel);
        pack();
    }
}
