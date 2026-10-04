import re

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
    cols = [line.strip().split()[0].replace('`', '') for line in users_match.group(1).split('\n') if line.strip() and not line.strip().startswith('PRIMARY') and not line.strip().startswith('INDEX') and not line.strip().startswith('UNIQUE')]
    print('Users columns:', cols)

# Check models columns
models_match = re.search(r'CREATE TABLE IF NOT EXISTS `models` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if models_match:
    cols = [line.strip().split()[0].replace('`', '') for line in models_match.group(1).split('\n') if line.strip() and not line.strip().startswith('PRIMARY') and not line.strip().startswith('INDEX') and not line.strip().startswith('UNIQUE')]
    print('Models columns:', cols)

# Check cash_agents columns
agents_match = re.search(r'CREATE TABLE IF NOT EXISTS `cash_agents` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if agents_match:
    cols = [line.strip().split()[0].replace('`', '') for line in agents_match.group(1).split('\n') if line.strip() and not line.strip().startswith('PRIMARY') and not line.strip().startswith('INDEX') and not line.strip().startswith('UNIQUE')]
    print('Cash agents columns:', cols)

# Check disputes columns
disputes_match = re.search(r'CREATE TABLE IF NOT EXISTS `disputes` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if disputes_match:
    cols = [line.strip().split()[0].replace('`', '') for line in disputes_match.group(1).split('\n') if line.strip() and not line.strip().startswith('PRIMARY') and not line.strip().startswith('INDEX') and not line.strip().startswith('UNIQUE')]
    print('Disputes columns:', cols)

# Check countries and payment_methods columns
countries_match = re.search(r'CREATE TABLE IF NOT EXISTS `countries` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if countries_match:
    cols = [line.strip().split()[0].replace('`', '') for line in countries_match.group(1).split('\n') if line.strip() and not line.strip().startswith('PRIMARY') and not line.strip().startswith('INDEX') and not line.strip().startswith('UNIQUE')]
    print('Countries columns:', cols)

pm_match = re.search(r'CREATE TABLE IF NOT EXISTS `payment_methods` \((.*?)\) ENGINE', schema, re.DOTALL | re.IGNORECASE)
if pm_match:
    cols = [line.strip().split()[0].replace('`', '') for line in pm_match.group(1).split('\n') if line.strip() and not line.strip().startswith('PRIMARY') and not line.strip().startswith('INDEX') and not line.strip().startswith('UNIQUE')]
    print('Payment methods columns:', cols)
