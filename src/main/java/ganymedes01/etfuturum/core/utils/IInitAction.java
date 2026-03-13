package ganymedes01.etfuturum.core.utils;

/**
 * Allows blocks/items to run specific code during load phases, without having to manually add them there. Only runs if
 * the content is enabled.
 * The funcs are not abstract by default since most things will only use one so it's better to just pull in the func
 * needed.
 * This avoids spamming a class that implements it with 3 empty functions.
 */
public interface IInitAction {

    default void preInitAction() {

    }

    default void initAction() {

    }

    default void postInitAction() {

    }

    default void onLoadAction() {

    }
}
