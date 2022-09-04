package com.nohit.jira_project.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.nohit.jira_project.model.*;
import com.nohit.jira_project.service.*;
import com.nohit.jira_project.util.*;

import static com.nohit.jira_project.constant.TemplateConstant.*;
import static com.nohit.jira_project.constant.ViewConstant.*;

@Controller
@RequestMapping(PRODUCT_VIEW)
public class SanPhamController {

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private SanPhamService productService;

    @Autowired
    private GioHangService gioHangService;

    // Fields
    private KhachHang mCurrentAccount;
    private boolean mIsByPass;

    // Load product
    @GetMapping("")
    public ModelAndView product() {
        var mav = new ModelAndView(PRODUCT_TEMP);
        GioHang gioHang;
        // check current account still valid
        if (!isValidAccount()) {
            gioHang = new GioHang();
        } else {
            var id = mCurrentAccount.getId();
            gioHang = gioHangService.getGioHang(id);
            // check gio_hang exist
            if (gioHang == null) {
                gioHang = new GioHang();
                gioHang.setId(id);
                gioHangService.saveGioHang(gioHang);
            }
        }
        var radioCheck = 0;
        mav.addObject("khachHang", mCurrentAccount);
        mav.addObject("gioHang", gioHang);
        mav.addObject("login", mCurrentAccount != null);
        mav.addObject("products", productService.getDsSanPham());
        mav.addObject("radioCheck", radioCheck);
        mIsByPass = false;
        return mav;
    }

    // Load top sale products
    @GetMapping(SORT_VIEW)
    public ModelAndView productSort(String sort) {
        var mav = new ModelAndView(PRODUCT_TEMP);
        GioHang gioHang;
        // check current account still valid
        if (!isValidAccount()) {
            gioHang = new GioHang();
        } else {
            var id = mCurrentAccount.getId();
            gioHang = gioHangService.getGioHang(id);
            // check gio_hang exist
            if (gioHang == null) {
                gioHang = new GioHang();
                gioHang.setId(id);
                gioHangService.saveGioHang(gioHang);
            }
        }
        List<SanPham> dsSanPham;
        int radioCheck;
        // filter function
        switch (sort) {
            case "topSale": {
                dsSanPham = productService.getDsSanPhamTopSale();
                radioCheck = 1;
                break;
            }
            case "newOrder": {
                dsSanPham = productService.getDsSanPhamNewestOrder();
                radioCheck = 2;
                break;
            }
            case "ascendingPriceOrder": {
                dsSanPham = productService.getDsSanPhamAscendingPriceOrder();
                radioCheck = 3;
                break;
            }
            case "descendingPriceOrder": {
                dsSanPham = productService.getDsSanPhamDescendingPriceOrder();
                radioCheck = 4;
                break;
            }
            default: {
                dsSanPham = productService.getDsSanPham();
                radioCheck = 0;
                break;
            }
        }
        mav.addObject("khachHang", mCurrentAccount);
        mav.addObject("gioHang", gioHang);
        mav.addObject("login", mCurrentAccount != null);
        mav.addObject("products", dsSanPham);
        mav.addObject("radioCheck", radioCheck);
        mIsByPass = false;
        return mav;
    }

    // Check valid account
    private boolean isValidAccount() {
        // check bypass
        if (mIsByPass) {
            return true;
        } else {
            mCurrentAccount = authenticationUtil.getAccount();
            return mCurrentAccount != null;
        }
    }
}
