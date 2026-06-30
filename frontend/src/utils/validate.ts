/**
 * 表单验证规则
 */
import type { FormItemRule } from 'element-plus'

/** 必填 */
export const required = (label = '此项'): FormItemRule => ({
  required: true,
  message: `${label}不能为空`,
  trigger: 'blur',
})

/** 必选 */
export const requiredSelect = (label = '此项'): FormItemRule => ({
  required: true,
  message: `请选择${label}`,
  trigger: 'change',
})

/** 长度限制 */
export const maxLength = (max: number): FormItemRule => ({
  max,
  message: `长度不能超过${max}个字符`,
  trigger: 'blur',
})

/** 正整数 */
export const positiveInteger: FormItemRule = {
  pattern: /^[1-9]\d*$/,
  message: '请输入正整数',
  trigger: 'blur',
}

/** 非负整数（含0） */
export const nonNegativeInteger: FormItemRule = {
  pattern: /^\d+$/,
  message: '请输入非负整数',
  trigger: 'blur',
}
