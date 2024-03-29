package org.dazeend.harmonium.screens;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.dazeend.harmonium.Harmonium;
import org.dazeend.harmonium.music.ArtSource;

import com.tivo.hme.bananas.BApplication;
import com.tivo.hme.bananas.BScreen;
import com.tivo.hme.sdk.ImageResource;
import com.tivo.hme.sdk.Resource;
import com.tivo.hme.sdk.View;

public abstract class HManagedResourceScreen extends BScreen {

	private Vector<Resource> _managedResources;
	private Vector<View> _managedViews;
	private ReentrantLock _lock = new ReentrantLock();
	private Boolean _freeOnExit = true;

	protected Harmonium app;
	
	public HManagedResourceScreen(BApplication app) {
		super(app);
		this.app = (Harmonium) app;
		_managedResources = new Vector<Resource>();
		_managedViews = new Vector<View>();
	}
	
	protected void doNotFreeResourcesOnExit()
	{
		_freeOnExit = false;
	}
	
	protected void setManagedResource(View view, Resource resource, int flags)
	{
		if (resource != null)
		{
			_managedResources.add(resource);
			if (this.app.isInSimulator())
				System.out.println("setManagedResource:" + resource.toString());
			view.setResource(resource, flags);
		}
	}
	
	protected void setManagedResource(View view, Resource resource)
	{
		if (resource != null)
		{
			_managedResources.add(resource);
			if (this.app.isInSimulator())
				System.out.println("setManagedResource:" + resource.toString());
			view.setResource(resource);
		}
	}
	
	// This view will be recursed and all its resources will be removed.
	protected void setManagedView(View view) {
		_managedViews.add(view);
	}
		
	private void cleanupManagedResources()
	{
		_lock.lock();
		try {
			for ( Resource r : _managedResources )
			{
				if (!app.getAlbumArtCache().Contains(r)) {
					if (app.isInSimulator())
						System.out.println("removeManagedResource:" + r.toString());
					r.remove();
				}
			}
			_managedResources.clear();

			for ( View v : _managedViews )
				cleanup(v);
			_managedViews.clear();
			
			this.remove();
			
			flush();
		} finally {
			_lock.unlock();
		}
	}
	
	protected ImageResource createManagedImage(ArtSource artSource, int width, int height) {
		return app.getAlbumArtCache().Add(this, artSource, width, height);
	}

	protected ImageResource createManagedImage(String arg0) {
		ImageResource ir = createImage(arg0);
		if (this.app.isInSimulator())
			System.out.println("createManagedImage:" + ir.toString());
		_managedResources.add(ir);
		return ir;
	}
	
	private void cleanup(View v) {
		if (v != null) {
			int childCount = v.getChildCount();
			for (int i = 0; i < childCount; i++)
				cleanup(v.getChild(i));
			Resource r = v.getResource();
			if (r != null && !(app.getAlbumArtCache().Contains(r)))
				r.remove();
		}
	}
	
	public void cleanup(){
		if (_freeOnExit)
		{
			new Thread() {
				public void run() {
					cleanupManagedResources();
				}
			}.start();
		}
	}
}
