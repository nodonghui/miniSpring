package org.example.mvc;

@Controller
public class HelloController {

	@GetMapping("/hello")
	public ModelAndView hello() {

		ModelAndView mv = new ModelAndView("hello.html");
		mv.addObject("message","Hello, MVC!");

		return mv;
	}
}
