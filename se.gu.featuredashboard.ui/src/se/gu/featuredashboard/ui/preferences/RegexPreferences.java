package se.gu.featuredashboard.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class RegexPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public RegexPreferences() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFieldEditors() {

		addField(new StringFieldEditor("txtNewLineRegex", "Line Annotation Regex", getFieldEditorParent()));
		addField(new StringFieldEditor("txtNewBeginRegex", "Begin Annotation Regex", getFieldEditorParent()));
		addField(new StringFieldEditor("txtNewEndRegex", "End Annotation Regex", getFieldEditorParent()));
	}

}
