package com.temporaryteam.noticeditor.io.importers;

/**
 *
 * @author aNNiMON
 * @param <R> result
 * @param <O> optional
 */
@FunctionalInterface
public interface ImportCallback<R, O> {

	public void call(R result, O optional);
}
