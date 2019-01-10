package tw.chiae.inlive.presentation.ui.widget.magicfilter.utils;


import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicAmaroFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicAntiqueFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicBeautyFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicBlackCatFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicBrannanFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicBrooklynFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicCalmFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicCoolFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicCrayonFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicEarlyBirdFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicEmeraldFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicEvergreenFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicFreudFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicHealthyFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicHefeFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicHudsonFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicImageAdjustFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicInkwellFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicKevinFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicLatteFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicLomoFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicN1977Filter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicNashvilleFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicNostalgiaFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicPixarFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicRiseFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicRomanceFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicSakuraFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicSierraFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicSketchFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicSkinWhitenFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicSunriseFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicSunsetFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicSutroFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicSweetsFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicTenderFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicToasterFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicValenciaFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicWaldenFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicWarmFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicWhiteCatFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.advanced.MagicXproIIFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.base.MagicLookupFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.base.gpuimage.GPUImageBrightnessFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.base.gpuimage.GPUImageContrastFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.base.gpuimage.GPUImageExposureFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.base.gpuimage.GPUImageFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.base.gpuimage.GPUImageHueFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.base.gpuimage.GPUImageSaturationFilter;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.base.gpuimage.GPUImageSharpenFilter;

public class MagicFilterFactory{

    public static GPUImageFilter initFilters(MagicFilterType type) {
        switch (type) {
            case NONE:
                return new GPUImageFilter();
            case WHITECAT:
                return new MagicWhiteCatFilter();
            case BLACKCAT:
                return new MagicBlackCatFilter();
            case SKINWHITEN:
                return new MagicSkinWhitenFilter();
            case BEAUTY:
                return new MagicBeautyFilter();
            case ROMANCE:
                return new MagicRomanceFilter();
            case SAKURA:
                return new MagicSakuraFilter();
            case AMARO:
                return new MagicAmaroFilter();
            case WALDEN:
                return new MagicWaldenFilter();
            case ANTIQUE:
                return new MagicAntiqueFilter();
            case CALM:
                return new MagicCalmFilter();
            case BRANNAN:
                return new MagicBrannanFilter();
            case BROOKLYN:
                return new MagicBrooklynFilter();
            case EARLYBIRD:
                return new MagicEarlyBirdFilter();
            case FREUD:
                return new MagicFreudFilter();
            case HEFE:
                return new MagicHefeFilter();
            case HUDSON:
                return new MagicHudsonFilter();
            case INKWELL:
                return new MagicInkwellFilter();
            case KEVIN:
                return new MagicKevinFilter();
            case LOCKUP:
                return new MagicLookupFilter("");
            case LOMO:
                return new MagicLomoFilter();
            case N1977:
                return new MagicN1977Filter();
            case NASHVILLE:
                return new MagicNashvilleFilter();
            case PIXAR:
                return new MagicPixarFilter();
            case RISE:
                return new MagicRiseFilter();
            case SIERRA:
                return new MagicSierraFilter();
            case SUTRO:
                return new MagicSutroFilter();
            case TOASTER2:
                return new MagicToasterFilter();
            case VALENCIA:
                return new MagicValenciaFilter();
            case XPROII:
                return new MagicXproIIFilter();
            case EVERGREEN:
                return new MagicEvergreenFilter();
            case HEALTHY:
                return new MagicHealthyFilter();
            case COOL:
                return new MagicCoolFilter();
            case EMERALD:
                return new MagicEmeraldFilter();
            case LATTE:
                return new MagicLatteFilter();
            case WARM:
                return new MagicWarmFilter();
            case TENDER:
                return new MagicTenderFilter();
            case SWEETS:
                return new MagicSweetsFilter();
            case NOSTALGIA:
                return new MagicNostalgiaFilter();
            case SUNRISE:
                return new MagicSunriseFilter();
            case SUNSET:
                return new MagicSunsetFilter();
            case CRAYON:
                return new MagicCrayonFilter();
            case SKETCH:
                return new MagicSketchFilter();
            //image adjust
            case BRIGHTNESS:
                return new GPUImageBrightnessFilter();
            case CONTRAST:
                return new GPUImageContrastFilter();
            case EXPOSURE:
                return new GPUImageExposureFilter();
            case HUE:
                return new GPUImageHueFilter();
            case SATURATION:
                return new GPUImageSaturationFilter();
            case SHARPEN:
                return new GPUImageSharpenFilter();
            case IMAGE_ADJUST:
                return new MagicImageAdjustFilter();
            default:
                return null;
        }
    }
}
