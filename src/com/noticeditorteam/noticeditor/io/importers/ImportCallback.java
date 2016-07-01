package com.noticeditorteam.noticeditor.io.importers;

/**
 * @param <R> result
 * @param <O> optional
 * @author aNNiMON
 */
@FunctionalInterface
public interface ImportCallback<R, O> {

    public void call(R result, O optional);
}
