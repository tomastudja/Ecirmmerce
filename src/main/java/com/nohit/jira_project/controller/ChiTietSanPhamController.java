package com.nohit.jira_project.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import com.nohit.jira_project.model.*;
import com.nohit.jira_project.service.*;
import com.nohit.jira_project.util.*;

import static com.nohit.jira_project.constant.AttributeConstant.*;
import static com.nohit.jira_project.constant.TemplateConstant.*;
import static com.nohit.jira_project.constant.ViewConstant.*;

import java.util.List;

@Controller
@RequestMapping(DETAIL_VIEW)
public class ChiTietSanPhamController {
    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private NhanXetService nhanXetService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    // Fields
    private KhachHang mCurrentAccount;
    private GioHang mCurrentCart;
    private String mMsg;
    private boolean mIsByPass;
    private boolean mIsMsgShow;

    @GetMapping("")
    public String detail() {
        return REDIRECT_PREFIX + PRODUCT_VIEW;
    }

    // Load detail
    @GetMapping(VIEW_VIEW)
    public ModelAndView detailFind(int id) {
        var mav = new ModelAndView(DETAIL_TEMP);
        // check current account still valid
        if (!isValidAccount()) {
            mCurrentCart = new GioHang();
        } else {
            var idKhacHang = mCurrentAccount.getId();
            mCurrentCart = gioHangService.getGioHang(idKhacHang);
            // check gio_hang exist
            if (mCurrentCart == null) {
                mCurrentCart = new GioHang();
                mCurrentCart.setId(idKhacHang);
                gioHangService.saveGioHang(mCurrentCart);
            }
        }
        mav.addObject("client", mCurrentAccount);
        mav.addObject("cart", mCurrentCart);
        mav.addObject("login", mCurrentAccount != null);
        mav.addObject("product", sanPhamService.getSanPham(id));
        mav.addObject("topPriceProducts", sanPhamService.getDsSanPhamDescendingDiscount().subList(0, 3));
        mav.addObject("topNewProducts", sanPhamService.getDsSanPhamNewest().subList(0, 3));
        mav.addObject("topSaleProducts", sanPhamService.getDsSanPhamTopSale().subList(0, 4));
        showMessageBox(mav);
        mIsByPass = false;
        return mav;
    }

    // Rate product
    @PostMapping(RATE_VIEW)
    public String detailRate(NhanXet nhanXet) {
        if (!isValidAccount()) {
            return REDIRECT_PREFIX + LOGIN_VIEW;
        } else {
            nhanXetService.saveNhanXet(nhanXet);
            mIsMsgShow = true;
            mMsg = "Nhận xét sản phẩm thành công!";
            mIsByPass = true;
            
            SanPham sanPham1 = sanPhamService.getSanPham(nhanXet.getIdSanPham());
            sanPham1.setDanhGia(Math.round((sanPham1.getDanhGia()+nhanXet.getDanhGia())/2));
            
            sanPhamService.saveSanPham(sanPham1);
            return REDIRECT_PREFIX + DETAIL_VIEW + "/view/?id=" + nhanXet.getIdSanPham();
        }
    }
    
    //
    private double calculateAverage(List <Integer> marks) {
        return marks.stream()
                    .mapToDouble(d -> d)
                    .average()
                    .orElse(0.0);
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

    // Show message
    private void showMessageBox(ModelAndView mav) {
        // check flag
        if (mIsMsgShow) {
            mav.addObject(FLAG_MSG_PARAM, true);
            mav.addObject(MSG_PARAM, mMsg);
            mIsMsgShow = false;
        }
    }
}
