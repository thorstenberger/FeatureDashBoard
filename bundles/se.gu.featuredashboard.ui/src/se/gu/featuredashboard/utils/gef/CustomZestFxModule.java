package se.gu.featuredashboard.utils.gef;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.zest.fx.ZestFxModule;

import com.google.inject.multibindings.MapBinder;

public class CustomZestFxModule extends ZestFxModule {
	
	@Override
	protected void bindNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindNodePartAdapters(adapterMapBinder);
		
		adapterMapBinder.addBinding(AdapterKey.role("OnClickOperation")).to(NodeOnClickPolicy.class);
	}
	
}
