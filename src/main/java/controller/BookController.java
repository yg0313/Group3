package controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import model.vo.BookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.impl.BookServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class BookController {

    @Autowired
    private BookServiceImpl bookService;

    @RequestMapping("showBook.ing")
    public String showBook(HttpServletRequest request, Model model) {
        model.addAttribute("isbn", request.getParameter("isbn"));   // 선택한 책에 맞는 isbn 넘겨줌
        return "showBook";
    }

    //선택한 ISBN의 도서 정보 보기
    @RequestMapping(value = "viewBook.ing")
    public String viewBook(BookVO vo, Model model) {
        model.addAttribute("viewBook", bookService.selectBook(vo));
        return "book/viewBook";
    }

    @RequestMapping("/searchBook.ing")
    public String searchBook(String keyword,
                             @RequestParam(defaultValue = "false") String sbox,
                             Model model) {
        HashMap map = new HashMap<>();

        map.put("find", keyword);

        if (!sbox.equals("false")) {
            System.out.println("sbox " + sbox);
            map.put("sbox", sbox);
        } else {
            System.out.println("sbox null");
            map.put("sbox", null);
        }

        List<BookVO> bookList = bookService.searchBook(map);

        model.addAttribute("keyword", keyword);
        model.addAttribute("result", bookList.size());
        model.addAttribute("bookList", bookList);
        return "book/bookList";
    }

    //페이지 넘김
    @RequestMapping("/{ing}")
    public String ing(@PathVariable String ing) {
        System.out.println("BookController에서" +ing + "요청");
        return ing;
        //return "book/" + ing;
    }

    @RequestMapping("/hello.ing")
    public String carousel(Model model){
        model.addAttribute("carouselBook",bookService.carouselBook());
        model.addAttribute("bestBook",bookService.bestBook());
        model.addAttribute("hotBook",bookService.hotBook());
        return "hello";
    }

    @RequestMapping("/insertBook.ing")
    public String insertBook(){
        return "book/insertBook";
    }

    //도서 입력 성공 페이지에서 도서목록보기
    @RequestMapping("/rbookList.ing")
    public String listBook() {
        System.out.println("listBook.ing 요청");
        return "redirect:bookList.ing";
    }

    // 도서입력
    @RequestMapping("/insertBook_success.ing")
    public String insertBook_success(BookVO vo) {
        System.out.println("insertBook_success.ing 요청");
        System.out.println(vo.getPublicationDate());
        bookService.insertBook(vo);
        System.out.println("성공");
        return "book/insertBook_success";
    }

    //도서 삭제하기
    @RequestMapping("/deleteBook_success.ing")
    public String deleteBook_success(BookVO vo) {
        System.out.println("deleteBook_success.ing 요청");
        bookService.deleteBook(vo);

        return "book/deleteBook_success";
    }

    //도서정보 수정하기
    @RequestMapping("/updateBook.ing")
    public String updateBook(BookVO vo) {
        System.out.printf("updateBook.ing");
        bookService.updateBook(vo);
        return "redirect:selectBook.ing?isbn=" + vo.getIsbn();
    }

    //도서 목록보기
    @RequestMapping("/bookList.ing")
    public String bookList(BookVO vo, Model model) {
        model.addAttribute("bookList", bookService.bookList(vo));
        return "book/bookList";
    }

    //선택한 ISBN의 도서정보 수정페이지
    @RequestMapping("/selectBook.ing")
    public String selectBook(BookVO vo, Model model) {
        model.addAttribute("selectBook", bookService.selectBook(vo));
        return "book/selectBook";
    }


    @RequestMapping(value = "/chartA.ing")
    public String chartA(Model model) throws Exception {
        System.out.println("chartA() 장르별 매출액 원 차트");
        List<Map<String, Object>> genreList = bookService.getGenreList();
        Map<String, Integer> maps  = new HashMap<String, Integer>();
        for (Map<String, Object> map : genreList) {
            System.out.println(map.get("GENRE"));
            System.out.println(map.get("PRICE"));
            String key = (String)map.get("GENRE");
            int value = ((BigDecimal)map.get("PRICE")).intValue();
            maps.put(key,value); // 임포트~
        }
        System.out.println("maps는" +maps);
        String result = "";
        Set<String> salesKeys = maps.keySet();
        for (String key : salesKeys) {
            if (result != "") {
                result += ",";
            }
            result += "['" + key + "', " + maps.get(key) + "]";
            //((BigDecimal) hm.get("AGE")).intValue()
        }

        model.addAttribute("chartA", result);
        return "book/chartA";
    }


    @RequestMapping("/genrebookList.ing")
    public String genrebookList(HttpServletRequest request, Model model){
        String genre = request.getParameter("genre");
        List<BookVO> list = bookService.genrebookList(genre);
        model.addAttribute("result", list.size());
        model.addAttribute("genre", genre);
        model.addAttribute("bookList", list);
        return  "book/genrebookList";
    }


}
