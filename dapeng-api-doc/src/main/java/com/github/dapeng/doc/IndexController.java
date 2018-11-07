package com.github.dapeng.doc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by tangliu on 2016/3/1.
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    @ModelAttribute
    public void populateModel(Model model) {
        model.addAttribute("tagName", "index");
    }

    @RequestMapping(path = "",method = RequestMethod.GET)
    public String index(HttpServletRequest request) {
        return "index";
    }

}