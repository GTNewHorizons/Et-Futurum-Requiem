package ganymedes01.etfuturum.client.gui;

public interface IGUIProgress {

    /*
    Progress lower than 0 disables the progress drawing. So only the progress text is shown.
    */
    void setProgress(int progress);


    /*
    If progress is lower than 0 and max progress is 0 no filled progress bar will be rendered.
    And if progress is lower than 0 and max progress is 1 a fully filled progress bar will be rendered.
     */
    void setMaxProgress(int maxProgress);

    /*
    Set a short description which task is running.
     */
    void setProgressText(String progressText);

    /*
    Set a flag for the result of the migration.
    0 = not done
    1 = successful
    2 = failed
    */
    void setSuccessful(byte successful);
}
