package webMail;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class TableModel extends AbstractTableModel {

	// Rewriting table model for class Mail
	private final String[] columnNames;
	private ArrayList<Mail> mails;

	public TableModel(String[] columnNames) {
		this.columnNames = columnNames;
		this.mails = new ArrayList<Mail>();
	}

	public void setData(ArrayList<Mail> mails) {
		this.mails = mails;
	}

	public ArrayList<Mail> getData() {
		return this.mails;
	}

	@Override
	public int getRowCount() {
		return this.mails.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (column == 0)
			return mails.get(row).getPriority();
		else if (column == 1)
			return mails.get(row).getFileEta();
		else if (column == 2)
			return mails.get(row).getSubject();
		else if (column == 3) {
			if (this.columnNames[3].equals("Sender"))
				return mails.get(row).getSender();
			else
				return mails.get(row).getRecipient();
		} else if (column == 4)
			return mails.get(row).getStringDate();
		else
			return null;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return String.class;
	}

	// Disable table editing
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void addRow(Mail mail) {
		this.mails.add(mail);
		this.fireTableDataChanged();
	}

	public void removeRow(int row) {
		this.mails.remove(row);
		this.fireTableDataChanged();
	}

	public Mail getMailAtRow(int row) {
		return mails.get(row);
	}
}
