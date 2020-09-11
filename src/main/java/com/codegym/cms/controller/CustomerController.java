package com.codegym.cms.controller;

import com.codegym.cms.model.Customer;
import com.codegym.cms.model.Province;
import com.codegym.cms.service.CustomerService;
import com.codegym.cms.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProvinceService provinceService;

    @ModelAttribute("provinces")
    public Iterable<Province> provinces() {
        return provinceService.findAll();
    }

    @GetMapping("/customers")
    public ModelAndView listCustomers(@RequestParam("s") Optional<String> s, Pageable pageable) {
        Page<Customer> customers;
        if (s.isPresent()) {
            customers = customerService.findAllByFirstNameContaining(s.get(), pageable);
        } else {
            customers = customerService.findAll(pageable);
        }
        ModelAndView modelAndView = new ModelAndView("/customer/list");
        modelAndView.addObject("customers", customers);
        return modelAndView;
    }

    @GetMapping("/create-customer")
    public ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/customer/create");
        modelAndView.addObject("customer", new Customer());
        return modelAndView;
    }

    @PostMapping("/create-customer")
    public ModelAndView saveCustomer(@Validated @ModelAttribute("customer") Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            ModelAndView modelAndView = new ModelAndView("/customer/create");
            modelAndView.addObject("message", "Cannot create new customer!");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("/customer/create");
        modelAndView.addObject("customer", new Customer());
        customerService.save(customer);
        modelAndView.addObject("message", "New customer created successfully");
        return modelAndView;

    }

    @GetMapping("/edit-customer/{id}")
    public ModelAndView showEditForm(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        ModelAndView modelAndView;
        if (customer != null) {
            modelAndView = new ModelAndView("/customer/edit");
            modelAndView.addObject("customer", customer);

        } else {
            modelAndView = new ModelAndView("/error.404");
        }
        return modelAndView;
    }

    @PostMapping("/edit-customer")
    public ModelAndView updateCustomer(@Validated @ModelAttribute("customer") Customer customer, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView("/customer/edit");
        modelAndView.addObject("customer", new Customer());
        if (bindingResult.hasFieldErrors()) {
            modelAndView.addObject("message", "Cannot create new customer!");
        } else {
            customerService.save(customer);
            modelAndView.addObject("message", "New customer created successfully");
        }
        return modelAndView;
    }

    @GetMapping("/delete-customer/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        ModelAndView modelAndView;
        if (customer != null) {
            modelAndView = new ModelAndView("/customer/delete");
            modelAndView.addObject("customer", customer);

        } else {
            modelAndView = new ModelAndView("/error.404");
        }
        return modelAndView;
    }

    @PostMapping("/delete-customer")
    public String deleteCustomer(@ModelAttribute("customer") Customer customer) {
        customerService.remove(customer.getId());
        return "redirect:customers";
    }
}
