import js from '@eslint/js'
import globals from 'globals'
import tseslint from 'typescript-eslint'
import pluginVue from 'eslint-plugin-vue'
import prettierPlugin from 'eslint-plugin-prettier'
import { defineConfig } from 'eslint/config'

export default defineConfig([
  {
    ignores: [
      '**/eslint.config.*',
      '**/.prettierrc*',
    ],
  },
  {
    files: ['**/*.{js,mjs,cjs,ts,mts,cts,vue}'],
    plugins: { js, prettier: prettierPlugin },
    extends: [
      'js/recommended',
       tseslint.configs.recommended,
       pluginVue.configs['flat/recommended'],
       pluginVue.configs['flat/strongly-recommended'],
    ],
    languageOptions: { globals: { ...globals.browser, ...globals.node } },
    rules: { 
      'prettier/prettier': 'error',
      'vue/singleline-html-element-content-newline': 'off',
      'vue/multiline-html-element-content-newline': 'off',
      'vue/max-attributes-per-line': 'off',
      'vue/html-closing-bracket-newline': 'off',
    },
  },
  {
    files: ['**/*.vue'],
    languageOptions: {
      parserOptions: { parser: tseslint.parser },
    },
  },
])