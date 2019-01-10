package tw.chiae.inlive.presentation.ui.base.page;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class PageRecorder {

    //记录当前页的游标
    private int currentPage;
    //页号生成器，用于实现不同需求的分页页号
    private PageNumGenerator generator;

    public PageRecorder() {
        this(new LinearPageGenerator(1, 1));
    }

    public PageRecorder(PageNumGenerator generator) {
        this.generator = generator;
        this.currentPage = generator.getFirstPage();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    //----- delegate methods BEGIN ------
    public int getNextPage() {
        return generator.getNextPage(currentPage);
    }

    public int getFirstPage() {
        return generator.getFirstPage();
    }
    //----- delegate methods END ------

    public synchronized void moveToFirstPage() {
        currentPage = generator.getFirstPage();
    }

    public synchronized void moveToNextPage() {
        currentPage = generator.getNextPage(currentPage);
    }

}
