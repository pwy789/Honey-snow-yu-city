module.exports = {
  // 解析器配置
  parser: 'vue-eslint-parser',
  parserOptions: {
      parser: '@typescript-eslint/parser',
      ecmaVersion: 2020,
      sourceType: 'module',
      ecmaFeatures: {
          jsx: true
      }
  },
  // 环境配置
  env: {
      browser: true,
      node: true,
      es6: true
  },
  // 扩展配置
  extends: [
      'eslint:recommended',
      'plugin:vue/vue3-recommended',
      'plugin:@typescript-eslint/recommended'
  ],
  // 插件配置
  plugins: [
      'vue',
      '@typescript-eslint'
  ],
  // 规则配置
  rules: {
      // 关闭组件必须是多行的规则检查
      'vue/max-len': 'off',
      // 关闭组件必须是 multi 的规则
      'vue/multi-word-component-names': 'off',
      // 允许未定义的变量，避免找不到 ElMessage 的报错
      'no-undef': 'off',
      // 如果你使用 TypeScript，可调整此规则
      '@typescript-eslint/no-undef': 'off'
  },
  // 全局变量配置
  globals: {
      // 声明 ElMessage 为只读全局变量
      ElMessageBox: 'readonly'
  }
};    