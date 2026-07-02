package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {

		List<OrderItem> orderItems = orderItemRepository.findAll();
		List<Item> items = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);
		//買い物かごにも商品リストにも商品が無かったら
		if (orderItems == null && items == null) {
			model.addAttribute("items", null);
			//もし買い物かごに何もなかったら新着順	
		} else if (orderItems == null) {
			model.addAttribute("sortType", Constant.DEFAULT_INDEX);
			model.addAttribute("items", items);
			//買い物かごに商品があれば売れ筋順で表示
		} else {
			model.addAttribute("items", itemRepository.findSalesCountBySellItemsAsc(Constant.NOT_DELETED));
		}
		return "index";
	}
	
	@RequestMapping(path = "/client/item/list/{page}", method = { RequestMethod.GET})
	public String itemList(@PathVariable Integer page,Model model) {
		//listへの渡し方を考え中。Page型だしsorttypeを見てまだよくわかってない。constantも要編集？
		return "client/item/list";
	}
}
