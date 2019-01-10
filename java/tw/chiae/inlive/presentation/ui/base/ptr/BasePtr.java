package tw.chiae.inlive.presentation.ui.base.ptr;

import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Handle base ptr header & footer for flexibility.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class BasePtr {
    /**
     * 设置默认的头部View，且设置为仅能刷新。
     */
    public static void setRefreshOnlyStyle(PtrFrameLayout ptrFrameLayout) {
        setDefaultHeader(ptrFrameLayout);
        ptrFrameLayout.setLoadingMinTime(1000);
        ptrFrameLayout.setDurationToClose(100);
        ptrFrameLayout.setMode(PtrFrameLayout.Mode.REFRESH);
    }

    /**
     * 设置默认的头部View，且设置为仅能刷新。
     */
    public static void setLoadMoreOnlyStyle(PtrFrameLayout ptrFrameLayout) {
        setDefaultFooter(ptrFrameLayout);
        ptrFrameLayout.setLoadingMinTime(1000);
        ptrFrameLayout.setDurationToClose(100);
        ptrFrameLayout.setMode(PtrFrameLayout.Mode.LOAD_MORE);
    }


    /**
     * 设置可上拉下拉刷新的View样式。
     */
    public static void setPagedPtrStyle(PtrFrameLayout ptrFrameLayout) {
        setDefaultHeader(ptrFrameLayout);
        setDefaultFooter(ptrFrameLayout);
        ptrFrameLayout.setLoadingMinTime(1000);
        ptrFrameLayout.setDurationToClose(100);
        ptrFrameLayout.setMode(PtrFrameLayout.Mode.BOTH);
    }

    public static void subscribeTestPtrHandler(final PtrFrameLayout ptrFrameLayout){
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrameLayout.refreshComplete();
                    }
                },1800);
            }
        });
    }

    private static void setDefaultHeader(PtrFrameLayout ptrFrameLayout) {
        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(ptrFrameLayout.getContext());
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
    }

    private static void setDefaultFooter(PtrFrameLayout ptrFrameLayout) {
        PtrClassicDefaultFooter footer = new PtrClassicDefaultFooter(ptrFrameLayout.getContext());
        ptrFrameLayout.setFooterView(footer);
        ptrFrameLayout.addPtrUIHandler(footer);
    }
}
