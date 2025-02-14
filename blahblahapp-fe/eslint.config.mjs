import eslint from '@eslint/js';
import eslintPluginVue from 'eslint-plugin-vue';
import typescriptEslint from 'typescript-eslint';
import vueEslintParser from "vue-eslint-parser";
import globals from 'globals';

// https://eslint.vuejs.org/user-guide/
export default typescriptEslint.config(
  { ignores: ["*", "!src", "!src/*", "!**/*.config.ts"] },
  {
    extends: [
      eslint.configs.recommended,
      ...typescriptEslint.configs.recommended,
      ...typescriptEslint.configs.stylistic,
      ...eslintPluginVue.configs['flat/recommended'],
    ],
    files: ['**/*.{ts,vue}'],
    languageOptions: {
      parser: vueEslintParser,
      ecmaVersion: "latest",
      sourceType: "module",
      globals: globals.browser,
      parserOptions: {
        parser: typescriptEslint.parser,
        project: ["./tsconfig.app.json", "./tsconfig.node.json"],
        extraFileExtensions: [".vue"],
      },
    },
    rules: {
      "vue/max-attributes-per-line": ["warn", {
        "singleline": { "max": 99 },
        "multiline": { "max": 1 }
      }],
      "arrow-parens": ["warn", "as-needed", {
        requireForBlockBody: true,
      }],
      "comma-dangle": ["warn", "only-multiline"],
      "curly": ["warn", "multi-line"],
      "eol-last": ["warn", "always"],
      "indent": ["warn", 2, {
        SwitchCase: 1,
        MemberExpression: 1,
      }],
      "key-spacing": "warn",
      "linebreak-style": ["warn", "windows"],
      "no-console": ["warn", {
        allow: ["debug", "warn", "error"],
      }],
      "no-trailing-spaces": "warn",
      "no-unreachable-loop": "error",
      "no-unused-vars": ["warn", {
        args: "none",
        destructuredArrayIgnorePattern: "^_",
      }],
      "no-var": "error",
      "object-curly-spacing": ["warn", "always"],
      "prefer-const": "warn",
      "quote-props": ["warn", "as-needed"],
      "quotes": ["warn", "single"],
      "semi": ["error", "always"],
      "valid-typeof": ["error", {
        requireStringLiterals: true,
      }],
    },
  }
);