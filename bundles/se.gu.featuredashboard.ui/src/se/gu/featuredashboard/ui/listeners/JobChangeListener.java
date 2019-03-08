package se.gu.featuredashboard.ui.listeners;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.ui.PlatformUI;

import se.gu.featuredashboard.utils.ICallbackEvent;
import se.gu.featuredashboard.utils.ICallbackListener;

public class JobChangeListener implements IJobChangeListener {

	private ICallbackListener callback;
	Logger logger = PlatformUI.getWorkbench().getService(org.eclipse.e4.core.services.log.Logger.class);
	
	public JobChangeListener(ICallbackListener callback) {
		this.callback = callback;
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
		logger.info("Parsing complete");
		IStatus status = (IStatus) event.getResult();
		if(status.getCode() == IStatus.OK) {
			ICallbackEvent callbackEvent = new ICallbackEvent(ICallbackEvent.EventType.ParsingComplete);
			callback.callbackMethod(callbackEvent);
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
