package com.temporaryteam.noticeditor.io.importers;

/**
 *
 * @author aNNiMON
 * @param <D> input data
 * @param <O> options
 * @param <R> result
 * 
 */
public interface Importer<D, O, R> {
	
	public void importFrom(D data, O options, ImportCallback<R, Exception> callback);
	
}
