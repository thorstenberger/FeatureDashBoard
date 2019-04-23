package se.gu.featuredashboard.utils.gef;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.zest.fx.ZestFxModule;

import com.google.inject.multibindings.MapBinder;

public class CustomZestFxModule extends ZestFxModule {
	
	@Override
	protected void bindEdgePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindEdgePartAdapters(adapterMapBinder);

		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(EdgeOnClickHandler.class);
	}

	@Override
	protected void bindNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindNodePartAdapters(adapterMapBinder);
		
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodeOnClickHandler.class);
	}
	
	@Override
	protected void bindGraphPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(LayoutContext.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CustomGraphLayoutContextBehavior.class);
	}

}
