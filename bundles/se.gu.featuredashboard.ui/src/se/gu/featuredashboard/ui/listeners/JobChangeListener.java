package se.gu.featuredashboard.ui.listeners;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.utils.IUpdateInformationListener;

public class JobChangeListener implements IJobChangeListener {

	private List<IUpdateInformationListener> listeners;
	private Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	public JobChangeListener(List<IUpdateInformationListener> listeners) {
		this.listeners = listeners;
	}
	
	@Override
	public void aboutToRun(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awake(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void done(IJobChangeEvent event) {
		logger.info("Finished parsing project");	
		IStatus status = (IStatus) event.getResult();
		if(status.getCode() == Status.OK_STATUS.getCode()) {
			listeners.forEach(listener -> {
				if(listener != null)
					listener.updateData();
			});
		}
	}

	@Override
	public void running(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scheduled(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sleeping(IJobChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
