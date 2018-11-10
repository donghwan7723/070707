package com.model2.mvc.service.purchase.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.purchase.PurchaseDAO;
import com.model2.mvc.service.purchase.PurchaseService;


@Service("purchaseServiceImpl")
public class PurchaseServiceImpl implements PurchaseService{
	
	//Field
	@Autowired
	@Qualifier("purchaseDaoImpl")
	private PurchaseDAO purchaseDao;
	public void setPurchaseDao(PurchaseDAO purchaseDao) {
		this.purchaseDao = purchaseDao;
	}
	
	//Constructor
	public PurchaseServiceImpl() {
		System.out.println(this.getClass());
	}
	
	public void addPurchase(Purchase purchase) throws Exception {
		System.out.println("purchaseServiceImpl addPurchase come in.......");
		purchaseDao.insertPurchase(purchase);
	}

	
	public Purchase getPurchase(int tranNo) throws Exception {
		System.out.println("purcahseServiceImpl getPurchase come in...."+tranNo);	
		return purchaseDao.findPurchase(tranNo);
	}

	public Map<String, Object> getPurchaseList(Search search, String buyerId) throws Exception {
		
		System.out.println("PurchaseServiceImpl의 getPurchaseList 메서드 시작점 ....");
		
		List<Purchase> list= purchaseDao.getPurchaseList(search, buyerId);
		System.out.println("1=====================================");
		int totalCount = purchaseDao.getTotalCount(buyerId);
		System.out.println("2=====================================");
		Map<String, Object> map = new HashMap<String, Object>();
	
		map.put("list", list);
		map.put("totalCount", new Integer(totalCount));
		return map;
	}
	
	
	public void updatePurchase(Purchase purchase) throws Exception {
		System.out.println("PurchaseServiceImpl의 updatePurchase Come in...........");
		purchaseDao.updatePurchase(purchase);
	}

	
	public void updateTranCode(Purchase purchase) throws Exception {
		System.out.println("PurchaseServiceImpl의 updateTranCode Come in.........");
		purchaseDao.updateTranCode(purchase);
	}

	
	public Purchase getPurchase2(int ProdNo) throws Exception {
		return null;
	}

	
	public Map<String, Object> getSaleList(Search search) throws Exception {
		return null;
	}

}