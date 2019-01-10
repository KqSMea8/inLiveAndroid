package tw.chiae.inlive.presentation.ui.base.page;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface PageNumGenerator {
    /**
     * 第一页页号。
     */
    int getFirstPage();

    /**
     * 根据当前页号生成下页。
     */
    int getNextPage(int current);
}