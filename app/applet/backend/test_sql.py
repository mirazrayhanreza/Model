import re
import os

with open('backend/schema.sql', 'r', encoding='utf-8') as f:
    schema = f.read()

tables = re.findall(r'CREATE TABLE IF NOT EXISTS [`"]?(\w+)[`"]?', schema, re.IGNORECASE)
print('Tables in schema.sql:', tables)

# Check all INSERT statements in schema.sql
inserts = re.findall(r'INSERT\s+(?:IGNORE\s+)?INTO\s+[`"]?(\w+)[`"]?', schema, re.IGNORECASE)
print('Inserts in schema.sql:', inserts)

# Check columns in users table
users_match = re.search(r'CREATE TABLE IF NOT EXISTS `users` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if users_match:
    cols = [line.strip().split()[0].replace('`', '') for line in users_match.group(1).split('\n') if line.strip() and not any(line.strip().startswith(k) for k in ['PRIMARY', 'INDEX', 'UNIQUE', 'KEY', 'CONSTRAINT'])]
    print('Users columns:', cols)

models_match = re.search(r'CREATE TABLE IF NOT EXISTS `models` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if models_match:
    cols = [line.strip().split()[0].replace('`', '') for line in models_match.group(1).split('\n') if line.strip() and not any(line.strip().startswith(k) for k in ['PRIMARY', 'INDEX', 'UNIQUE', 'KEY', 'CONSTRAINT'])]
    print('Models columns:', cols)

agents_match = re.search(r'CREATE TABLE IF NOT EXISTS `cash_agents` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if agents_match:
    cols = [line.strip().split()[0].replace('`', '') for line in agents_match.group(1).split('\n') if line.strip() and not any(line.strip().startswith(k) for k in ['PRIMARY', 'INDEX', 'UNIQUE', 'KEY', 'CONSTRAINT'])]
    print('Cash agents columns:', cols)

disputes_match = re.search(r'CREATE TABLE IF NOT EXISTS `disputes` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if disputes_match:
    cols = [line.strip().split()[0].replace('`', '') for line in disputes_match.group(1).split('\n') if line.strip() and not any(line.strip().startswith(k) for k in ['PRIMARY', 'INDEX', 'UNIQUE', 'KEY', 'CONSTRAINT'])]
    print('Disputes columns:', cols)

countries_match = re.search(r'CREATE TABLE IF NOT EXISTS `countries` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if countries_match:
    cols = [line.strip().split()[0].replace('`', '') for line in countries_match.group(1).split('\n') if line.strip() and not any(line.strip().startswith(k) for k in ['PRIMARY', 'INDEX', 'UNIQUE', 'KEY', 'CONSTRAINT'])]
    print('Countries columns:', cols)

pm_match = re.search(r'CREATE TABLE IF NOT EXISTS `payment_methods` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if pm_match:
    cols = [line.strip().split()[0].replace('`', '') for line in pm_match.group(1).split('\n') if line.strip() and not any(line.strip().startswith(k) for k in ['PRIMARY', 'INDEX', 'UNIQUE', 'KEY', 'CONSTRAINT'])]
    print('Payment methods columns:', cols)
