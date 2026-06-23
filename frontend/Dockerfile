FROM node:18-alpine
WORKDIR /app

# Copy dependency files first to cache them
COPY package.json package-lock.json* ./
RUN npm install

# Copy the rest of the frontend code
COPY . .

EXPOSE 5173
CMD ["npm", "run", "dev", "--", "--host"]
