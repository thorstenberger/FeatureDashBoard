package se.gu.featuredashboard.utils;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ErrorDialog extends TitleAreaDialog {

	private String title;
	private String message;
	private String content;
	
	protected ErrorDialog(Shell parentShell, String title, String message, String content) {
		super(parentShell);
		this.title = title;
		this.message = message;
		this.content = content;
	}
	
	@Override
	public void create() {
		super.create();
		setTitle(title);
		setMessage(message, IMessageProvider.INFORMATION);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);

		Label errorMessage = new Label(area, SWT.NONE);
		errorMessage.setText(content);
		errorMessage.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return area;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(600, 300);
	}
	
	@Override
    protected boolean isResizable() {
        return true;
    }
	
}
