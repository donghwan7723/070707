package com.model2.mvc.web.product;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;

//==> 상품관리 Controller
@Controller
@RequestMapping("/product/*")
public class ProductController{
	
	//Field
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	
	public ProductController() {
		System.out.println(this.getClass());
	}
	
	@Value("#{commonProperties['pageUnit']}")
		int pageUnit;	
	
	@Value("#{commonProperties['pageSize']}")
		int pageSize;
	
	@RequestMapping(value = "addProduct", method=RequestMethod.POST)
	public String addProduct (@ModelAttribute("product") Product product) throws Exception {
	
		System.out.println("여기는 productController addProduct");
		
		
		
		//Business Logic
		productService.addProduct(product);

		return "forward:/product/addProduct.jsp";
	}
		
	@RequestMapping(value="listProduct")
	public String listProduct(@ModelAttribute("search") Search search, Model model, HttpServletRequest request) throws Exception {
		
		System.out.println("여기는 productController listProduct");
		
		System.out.println("==========="+search);
		
		
		if(search.getCurrentPage()==0) {
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		System.out.println("==========="+search);
		
		
		//Business Logic 수행
		Map<String, Object> map = productService.getProductList(search);
		
		Page resultPage = new Page(search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);

		//Model과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		System.out.println("여기는 productController listProduct 끝지점");
		
		return "forward:/product/listProduct.jsp";
	}
	
		@RequestMapping(value="getProduct", method=RequestMethod.GET)
		public String getProduct (@ModelAttribute("product") Product product, @RequestParam("prodNo") String prodNo, 
								  Model model, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		System.out.println("여기는 productController getProduct");
		
		product = productService.getProduct(product.getProdNo());
		
		model.addAttribute("product", product);
		
		Cookie[] cookies = request.getCookies();
		if(cookies!=null && cookies.length>0) {
			System.out.println("기존 Cookie 이용");
		  for(int i=0;i<cookies.length;i++) {	
			  Cookie cookie = cookies[i];
			if(cookie.getName().equals("history")) {
				cookie.setValue(cookie.getValue()+","+prodNo);
				cookie.setPath("/");
				cookie.setMaxAge(60*60);
				response.addCookie(cookie);
			}else{
			System.out.println("Cookie 첫 생성");
			cookie = new Cookie("history",prodNo);
			cookie.setMaxAge(60*60);
			cookie.setPath("/");
			response.addCookie(cookie);
			}
		  }
		}
		
		
		return "forward:/product/getProduct.jsp"; 
	}
		
		@RequestMapping(value="updateProductView", method=RequestMethod.GET)
		public String updateProductView(@RequestParam("prodNo") int prodNo, Model model) throws Exception {
			
			System.out.println("여기는 productController updateProductView");
			
			Product product = productService.getProduct(prodNo);
			
			model.addAttribute("product", product);
		
			return "forward:/product/updateProductView.jsp";
		}
		
		@RequestMapping(value="updateProduct", method=RequestMethod.POST)
		public String updateProduct(@ModelAttribute("product") Product product, 
									@RequestParam("menu") String menu) throws Exception {
			
			System.out.println("여기는 productController updateProduct");
			
			System.out.println("디버깅용"+product);
			System.out.println("디버깅용"+menu);
					
			productService.updateProduct(product);
			
		
			return "redirect:/product/getProduct?prodNo="+product.getProdNo()+"&menu="+menu;
		}
		



}
