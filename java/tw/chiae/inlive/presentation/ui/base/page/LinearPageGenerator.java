package tw.chiae.inlive.presentation.ui.base.page;

/**
 * 最常用的线性页号生成。
 */
public class LinearPageGenerator implements PageNumGenerator {

    private int pageStart;
    private int pageStep;

    public LinearPageGenerator(int pageStart, int pageStep) {
        this.pageStart = pageStart;
        this.pageStep = pageStep;
    }

    @Override
    public int getNextPage(int current) {
        return current + pageStep;
    }

    @Override
    public int getFirstPage() {
        return pageStart;
    }
}