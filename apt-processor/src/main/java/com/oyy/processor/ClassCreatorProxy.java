package com.oyy.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * create by ouyangyi@manqian.cn
 * on 2019/2/26 17:11
 * Description: Java文件代理类
 */
public class ClassCreatorProxy {

    private String packageName;

    private String className;

    private TypeElement typeElement;

    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();

    public ClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.typeElement = classElement;

        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        this.packageName = packageElement.getQualifiedName().toString();
        this.className = classElement.getSimpleName().toString() + "_ViewBinding";

    }

    public void putElement(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }

    /**
     * 创建Java代码
     *
     * @return
     */
    public String generateJavaCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package ").append(packageName).append(";\n");
        stringBuilder.append("public class ").append(className).append("{ \n");

        generateMethods(stringBuilder);

        stringBuilder.append("}\n");

        return stringBuilder.toString();
    }

    /**
     * 加入Method
     *
     * @param builder
     */
    private void generateMethods(StringBuilder builder) {

        builder.append("public void bind(" + typeElement.getQualifiedName() + " host ) { \n");

        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));\n");
        }

        builder.append("  }\n");

    }

    public String getProxyClassFullName() {
        return packageName + "." + className;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }


    /**
     * 创建Java代码
     *
     * @return
     */
    public TypeSpec generateJavaCode2() {
        TypeSpec bindingClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods2())
                .build();
        return bindingClass;
    }

    /**
     * 加入Method
     */
    private MethodSpec generateMethods2() {
        ClassName host = ClassName.bestGuess(typeElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(host, "host");
        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addCode("host." + name + " = " + "(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));\n");
        }
        return methodBuilder.build();
    }

    public String getPackageName() {
        return packageName;
    }

}
