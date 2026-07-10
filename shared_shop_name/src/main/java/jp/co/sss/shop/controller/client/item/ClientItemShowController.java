package jp.co.sss.shop.controller.client.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
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
			//もし注文一覧に何もなかったら新着順	
		} else if (orderItems == null) {
			model.addAttribute("sortType", Constant.DEFAULT_INDEX);
			model.addAttribute("items", items);
			//注文一覧に商品があれば売れ筋順で表示
		} else {
			model.addAttribute("items", itemRepository.findSalesCountBySellItemsAsc(Constant.NOT_DELETED));
		}
		return "index";
	}
	
	/**
	 * 商品一覧画面 表示処理
	 * @param sortTytpe  表示順を受け取る
	 * @param categoryId サイドバーから検索したいカテゴリのIDを受け取る
	 * @param model      Viewとの値受渡し
	 * @return "client/item/list"   商品一覧画面
	 */

	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET,RequestMethod.POST })
	public String itemList(@PathVariable Integer sortType, @RequestParam(required = false) Integer categoryId,
			Model model) {

		List<Item> items = new ArrayList<>();

		if (categoryId == null) {
			//カテゴリが選択されていない場合。1は新着順、2は売れ筋順で渡す。
			if (sortType == 1) {
				items = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);
			} else if (sortType == 2) {
				items = itemRepository.findSalesCountBySellItemsAsc(Constant.NOT_DELETED);
			}
			//カテゴリが選択された場合。1は新着順、2は売れ筋順で渡す。
		} else {
			if (sortType == 1) {
				items = itemRepository.findSalesCountAndCategoryBySellItemsAsc(Constant.NOT_DELETED,
						categoryId);
			} else if (sortType == 2) {
				items = itemRepository.findSalesCountAndCategoryBySellItemsAsc(Constant.NOT_DELETED,
						categoryId);
			}
		}

		List<ItemBean> itemBeans = new ArrayList<>();
		for (Item item : items) {
			ItemBean itemBean = beanTools.copyEntityToItemBean(item);
			itemBeans.add(itemBean);
		}
		model.addAttribute("items", itemBeans);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("sortType", sortType);
		return "client/item/list";
	}
	
	/**
	 * 商品詳細画面 表示処理
	 * @param id  　　　　　　商品IDを受け取る
	 * @param model      Viewとの値受渡し
	 * @return "client/item/detail"   商品詳細画面
	 */

	@RequestMapping(path = "/client/item/detail/{id}", method = { RequestMethod.GET })
	public String itemDetail(@PathVariable Integer id,Model model) {

		Item item = itemRepository.getReferenceById(id);
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);
		model.addAttribute("item", itemBean);
		return "client/item/detail";
	}
	
}
