package org.rrgames.underground.dev;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import org.rrgames.underground.Main;

import java.util.function.Consumer;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class EasyBrigadier<S> {

    private static CommandDispatcher<?> dispatcher;

    public static <S> void init(CommandDispatcher<S> disp) {
        dispatcher = disp;
    }

    @SuppressWarnings("unchecked")
    public static <S> LiteralCommandNode<S> registerCommand(
            String name,
            Consumer<CommandContext<S>> executor,
            Class<?>... args) {

        LiteralArgumentBuilder<S> root = literal(name);
        ArgumentBuilder<S, ?> current = root;

        int index = 0;

        for (Class<?> clazz : args) {
            RequiredArgumentBuilder<S, ?> arg = argument("arg" + index++, map(clazz));

            current.then(arg);
            current = arg;
        }

        current.executes(ctx -> {
            executor.accept(ctx);
            return 1;
        });

        return ((CommandDispatcher<S>) dispatcher).register(root);
    }

    private static ArgumentType<?> map(Class<?> clazz) {

        if (clazz == String.class)
            return StringArgumentType.word();

        if (clazz == Integer.class || clazz == int.class)
            return IntegerArgumentType.integer();

        if (clazz == Double.class || clazz == double.class)
            return DoubleArgumentType.doubleArg();

        if (clazz == Boolean.class || clazz == boolean.class)
            return BoolArgumentType.bool();

        throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
    }

    public static void main(String[] args) {
        CommandDispatcher<Object> dispatcher = new CommandDispatcher<>();
        EasyBrigadier.init(dispatcher);

        EasyBrigadier.registerCommand(
                "version",
                ctx -> System.out.println(Main.VERSION),
                String.class);
    }
}
