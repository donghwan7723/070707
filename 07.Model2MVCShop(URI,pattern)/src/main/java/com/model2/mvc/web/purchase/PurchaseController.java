package com.model2.mvc.web.purchase;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.purchase.PurchaseService;

@Controller
@RequestMapping("/purchase/*")
public class PurchaseController {
	
	//Field
	@Autowired
	@Qualifier("purchaseServiceImpl")
	private PurchaseService purchaseService;
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	public PurchaseController() {
		System.out.println(this.getClass());
	}
	
	@Value("#{commonProperties['pageUnit']}")
		int pageUnit;	

	@Value("#{commonProperties['pageSize']}")
		int pageSize;
	
	@RequestMapping(value = "addPurchase", method=RequestMethod.GET)
	public ModelAndView addPurchase(@RequestParam("prod_no") int prodNo) throws Exception{
		
		Product product = productService.getProduct(prodNo);
		
		String viewName = "/purchase/addPurchaseView.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
		modelAndView.addObject("product", product);
		
		return modelAndView; 
	}
	@RequestMapping(value = "addPurchase", method=RequestMethod.POST)
	public ModelAndView addPurchase(@ModelAttribute("product") Product product, 
									@ModelAttribute("purchase") Purchase purchase, HttpSession session) throws Exception{
		User user = (User)session.getAttribute("user");
		String tranCode = "1";
		
		System.out.println("디버깅 확인 : "+user);
		System.out.println(product);

		purchase.setPurchaseProd(product);
		purchase.setBuyer(user);
		purchase.setTranCode(tranCode);
			
		purchaseService.addPurchase(purchase);

		String viewName = "/purchase/addPurchase.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
		modelAndView.addObject("product", product);
		
		return modelAndView;
	}
	
	@RequestMapping(value = "getPurchase")
	public ModelAndView getPurchase(@ModelAttribute("purchase") Purchase purchase, 
			  						@RequestParam("tranNo") int tranNo) throws Exception{
		
		
		purchase = purchaseService.getPurchase(tranNo);
		
		String viewName = "/purchase/updatePurchaseView.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
		modelAndView.addObject("purchase", purchase);
		
		return modelAndView;	
	}
	
	@RequestMapping(value="listPurchase")
	public ModelAndView listPurchase(@ModelAttribute("search") Search search, @ModelAttribute("purchase") Purchase purchase,
									 HttpSession session) throws Exception{
			
		User user = (User)session.getAttribute("user");	
		purchase.setBuyer(user);
				
		if(search.getCurrentPage()==0) {
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		
		Map<String, Object> map = purchaseService.getPurchaseList(search, purchase.getBuyer().getUserId());
		Page resultPage = new Page(search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(),
									pageUnit, pageSize);
		
		String viewName ="/purchase/listPurchase.jsp";
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(viewName);
		modelAndView.addObject("list",map.get("list"));
		modelAndView.addObject("resultPage", resultPage);
		modelAndView.addObject("search", search);
		
		return modelAndView;
		
	}
	
	
	@RequestMapping(value = "updatePurchase", method=RequestMethod.GET)
	public ModelAndView updatePurchase(@ModelAttribute("purchase") Purchase purchase, 
									   @RequestParam("tranNo") int tranNo) throws Exception{
		
		
		purchase = purchaseService.getPurchase(tranNo);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/updatePurchaseView.jsp");
		modelAndView.addObject("purchase" , purchase);
		
		return modelAndView;
	}

	@RequestMapping(value = "updatePurchase", method=RequestMethod.POST)
	public ModelAndView updatePurchase(@ModelAttribute("purchase") Purchase purchaseUP, @RequestParam("tranNo") int tranNo,
								 @ModelAttribute("purchase") Purchase purchase) throws Exception{
	
		System.out.println("PurchaseController의 updatePurchase 메서드 시작");
		purchaseUP.setTranNo(tranNo);
		purchaseService.updatePurchase(purchaseUP);
		
		purchase = purchaseService.getPurchase(tranNo);
	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/purchase/updatePurchase.jsp");
		modelAndView.addObject("purchase" , purchase);
		
		return modelAndView;
	}
	
	@RequestMapping(value="updateTranCodeByProd")
	public ModelAndView updateTranCodeByProd(@ModelAttribute("product") Product product,
										@ModelAttribute("purchase") Purchase purchase, @RequestParam("prodNo") int prodNo
									   , @RequestParam("tranCode") String tranCode) throws Exception {
		
		System.out.println("                                "+tranCode);
		product.setProdNo(prodNo);
		purchase.setPurchaseProd(product);
		purchase.setTranCode(tranCode);
		
		purchaseService.updateTranCode(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/product/listProduct?menu=manage");
		
		return modelAndView;
	}
	

	@RequestMapping(value="updateTranCode")
	public ModelAndView updateTranCode(@ModelAttribute("purchase") Purchase purchase, 
										@RequestParam("tranCode") String tranCode, 
										@RequestParam("tranNo") int tranNo) throws Exception {
		
		purchase.setTranCode(tranCode);
		purchase.setTranNo(tranNo);
		
		purchaseService.updateTranCode(purchase);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/purchase/listPurchase?menu=search");
		
		return modelAndView;
	}

}
