package mau.donate.controller.admin;

import mau.donate.objects.User;
import mau.donate.service.DatabaseObject;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static mau.donate.controller.AppController.addEssential;

@CrossOrigin(origins = "*")
@Controller
public class DBEditorController {

    public static final String DBObjectPackage = "mau.donate.objects.";


    @RequestMapping("/admin/edit/{item}")
    public String adminEdit(Model model, Principal loggedUser, @PathVariable String item) {
        return adminEdit(model, loggedUser, item, null);
    }

    @RequestMapping("/admin/edit/{item}/{id}")
    public String adminEdit(Model model, Principal loggedUser, @PathVariable String item, @PathVariable Long id) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);
        try {
            item = item.substring(0,1).toUpperCase() + item.substring(1);
            List<FieldMeta> fields = new ArrayList<>();
            Class<?> objClass = Class.forName(DBObjectPackage + item);
            Object entity = id != null ? DatabaseObject.getById(objClass, id).orElseThrow() : null;
            for (Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (Modifier.isTransient(field.getModifiers())) continue;
                if (field.getName().endsWith("At")) continue;
                if (field.getName().endsWith("Password")) continue;

                FieldMeta meta = new FieldMeta();
                meta.name = field.getName();
                meta.type = field.getType().getSimpleName();
                if (entity != null) meta.value = field.get(entity);
                fields.add(meta);
            }
            UniversalForm form = new UniversalForm();
            form.fields = fields;
            model.addAttribute("form", form);

            model.addAttribute("item", item);
            model.addAttribute("id", id);
        } catch (Exception ignored) {
            return "redirect:/admin?page=3&errorDb";
        }
        return "editor";
    }

    @PostMapping("/admin/update/{item}/{id}")
    public String editItem(Model model, Principal loggedUser, RedirectAttributes redirectAttributes, @PathVariable String item, @PathVariable Long id, @ModelAttribute UniversalForm form) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);
        try {
            item = item.substring(0,1).toUpperCase() + item.substring(1);
            @SuppressWarnings("unchecked")
            Class<? extends DatabaseObject<?>> objClass = (Class<? extends DatabaseObject<?>>) Class.forName(DBObjectPackage + item).asSubclass(DatabaseObject.class);
            DatabaseObject<?> entity = id != null ? DatabaseObject.getById(objClass, id).orElseThrow() : null;
            if (entity == null) {
                Constructor<DatabaseObject<?>> ctor = (Constructor<DatabaseObject<?>>) objClass.getDeclaredConstructor();
                ctor.setAccessible(true);
                entity = ctor.newInstance();
            }

            for (FieldMeta entry : form.fields) {
                Field field = entity.getClass().getDeclaredField(entry.name);
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (Modifier.isTransient(field.getModifiers())) continue;
                if (field.getName().endsWith("At")) continue;
                if (field.getName().endsWith("Password")) continue;
                if (field.getName().endsWith("ID")) continue;

                Convert(field, entity, entry.value);
            }
            objClass.getField("UpdatedAt").set(entity, java.time.LocalDateTime.now());
            if (id == null) {
                entity = (DatabaseObject<?>) entity.WriteThenReturn().orElse(null);
                id = entity.getID();
            } else {
                entity.Update();
            }
            redirectAttributes.addFlashAttribute("success", "Entry updated successfully.");
            return "redirect:/admin/" + item + "/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/admin/" + item + "/" + id;
        }
    }

    public void Convert(Field field, Object entity, Object value) {
        try {
            Type type = field.getType();
            if (type.equals(String.class)) {
                field.set(entity, value.toString());
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                field.set(entity, Integer.parseInt(value.toString()));
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                field.set(entity, Long.parseLong(value.toString()));
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                field.set(entity, Double.parseDouble(value.toString()));
            } else if (type.equals(Float.class) || type.equals(float.class)) {
                field.set(entity, Float.parseFloat(value.toString()));
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                if (value instanceof String) {
                    field.set(entity, Boolean.parseBoolean((String)value));
                } else {
                    field.set(entity, value);
                }
            } else if (type.equals(LocalDate.class)) {
                field.set(entity, LocalDate.parse(value.toString())); // optionally use formatter
            } else if (type.equals(LocalDateTime.class)) {
                field.set(entity, LocalDateTime.parse(value.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            } else {
                field.set(entity, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class UniversalForm {
        public List<FieldMeta> fields;

        public List<FieldMeta> getFields() {
            return fields;
        }
        public void setFields(List<FieldMeta> fields) {
            this.fields = fields;
        }
    }

    public static class FieldMeta {
        public String name;
        public String type;
        public Object value = null;
        public Boolean booleanValue = null;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }

        public Object getValue() {
            return value;
        }
        public void setValue(Object value) {
            this.value = value;
        }


        public Boolean getBooleanValue() {
            return (value instanceof Boolean) ? (Boolean) value : false;
        }
        public void setBooleanValue(Boolean value) {
            this.value = value;
        }
    }
}
