package com.nohit.jira_project.service;

import java.io.*;
import java.util.*;

import javax.mail.*;

import com.nohit.jira_project.model.*;

public interface KhachHangService {
    public List<KhachHang> getDsKhachHang();

    public KhachHang getKhachHang(int id);

    public KhachHang getKhachHang(String email);

    public void saveKhachHang(KhachHang khachHang);

    public void saveKhachHangWithoutPassword(KhachHang khachHang);

    public void updatePassword(int id, String password);

    public void deleteKhachHang(int id);

    public void resetPassword(String email) throws UnsupportedEncodingException, MessagingException;
}
